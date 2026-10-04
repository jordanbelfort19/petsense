package com.example.pet.ui.pets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pet.R;
import com.example.pet.data.Pet;

import java.util.ArrayList;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    public interface OnPetClickListener {
        void onPetClicked(Pet pet);
    }

    private List<Pet> pets = new ArrayList<>();
    private String selectedPetId;
    private final OnPetClickListener listener;

    public PetAdapter(OnPetClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Pet> newPets, String selectedId) {
        this.pets = newPets != null ? newPets : new ArrayList<>();
        this.selectedPetId = selectedId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet_row, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = pets.get(position);
        holder.name.setText(pet.getName());

        String meta = pet.getSpecies();
        if (pet.getBreed() != null && !pet.getBreed().trim().isEmpty()) {
            meta = pet.getSpecies() + " \u00b7 " + pet.getBreed();
        }
        holder.meta.setText(meta);

        boolean isSelected = pet.getId() != null && pet.getId().equals(selectedPetId);
        holder.selectedCheck.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> listener.onPetClicked(pet));
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        final ImageView avatar;
        final TextView name;
        final TextView meta;
        final ImageView selectedCheck;

        PetViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.ivPetRowAvatar);
            name = itemView.findViewById(R.id.tvPetRowName);
            meta = itemView.findViewById(R.id.tvPetRowMeta);
            selectedCheck = itemView.findViewById(R.id.ivPetRowSelected);
        }
    }
}
