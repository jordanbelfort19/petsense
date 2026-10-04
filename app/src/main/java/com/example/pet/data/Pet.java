package com.example.pet.data;

import androidx.annotation.Keep;

/**
 * A single pet profile. Plain POJO on purpose — Firestore needs a public
 * no-arg constructor and public getters (or matching field names) to
 * deserialize documents automatically via DocumentSnapshot.toObject(Pet.class).
 */
@Keep
public class Pet {

    private String id;          // Firestore document id — set after save, not before
    private String name;
    private String species;     // one of ps_species_options: Dog, Cat, Bird, Rabbit, Fish, Other
    private String breed;       // optional
    private String age;         // free-text, e.g. "2 years" or "6 months" — kept simple on purpose
    private String notes;       // optional
    private boolean isMale;     // used to pick the existing gender icon; default true
    private String weight;      // e.g. "24 kg"
    private String photoUrl;    // local URI or remote URL for the pet's photo

    /** Required no-arg constructor for Firestore deserialization. */
    public Pet() {
    }

    public Pet(String name, String species, String breed, String age, String notes, boolean isMale, String weight, String photoUrl) {
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.notes = notes;
        this.isMale = isMale;
        this.weight = weight;
        this.photoUrl = photoUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isMale() { return isMale; }
    public void setMale(boolean male) { isMale = male; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    /** "Golden Retriever · 2 Years" style line for the pet summary card, or "Dog · 2 Years" if no breed. */
    public String breedAgeLine() {
        String left = (breed != null && !breed.trim().isEmpty()) ? breed.trim() : species;
        if (age == null || age.trim().isEmpty()) {
            return left;
        }
        return left + " \u00b7 " + age.trim();
    }
}
