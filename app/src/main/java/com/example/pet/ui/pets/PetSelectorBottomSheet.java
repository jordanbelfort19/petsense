package com.example.pet.ui.pets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pet.data.Pet;
import com.example.pet.databinding.BottomSheetPetSelectorBinding;
import com.example.pet.ui.dashboard.DashboardViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Lists the signed-in user's saved pets (name, species, small avatar), highlights the
 * active one, and switches the active pet on tap. Also exposes "Add a new pet", which
 * both this sheet and the dashboard's own + button route to the same AddPetActivity flow.
 */
public class PetSelectorBottomSheet extends BottomSheetDialogFragment {

    public interface OnAddPetRequestedListener {
        void onAddPetRequested();
    }

    private BottomSheetPetSelectorBinding binding;
    private DashboardViewModel viewModel;
    private PetAdapter adapter;
    private OnAddPetRequestedListener addPetListener;

    public static PetSelectorBottomSheet newInstance() {
        return new PetSelectorBottomSheet();
    }

    public void setOnAddPetRequestedListener(OnAddPetRequestedListener listener) {
        this.addPetListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = BottomSheetPetSelectorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Shared with MainActivity because both are scoped to the same Activity —
        // this is how the sheet sees the same pet list/selection without re-fetching.
        viewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        adapter = new PetAdapter(pet -> {
            viewModel.selectPet(pet);
            dismiss();
        });
        binding.rvPetSelectorList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPetSelectorList.setAdapter(adapter);

        viewModel.getPets().observe(getViewLifecycleOwner(), pets -> {
            Pet selected = viewModel.getSelectedPet().getValue();
            String selectedId = selected != null ? selected.getId() : null;
            adapter.submitList(pets, selectedId);
        });

        binding.rowAddNewPet.setOnClickListener(v -> {
            dismiss();
            if (addPetListener != null) {
                addPetListener.onAddPetRequested();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
