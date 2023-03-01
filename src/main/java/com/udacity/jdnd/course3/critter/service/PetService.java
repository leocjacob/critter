package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.CustomerEntity;
import com.udacity.jdnd.course3.critter.entity.PetEntity;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
public class PetService {
    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;

    public PetService(PetRepository petRepository, CustomerRepository customerRepository) {
        this.petRepository = petRepository;
        this.customerRepository = customerRepository;
    }


    /**
     * Save pet data in database
     * @param petDTO
     * @return
     */
    public PetDTO savePet(PetDTO petDTO){
        try {
            PetEntity petEntity = new PetEntity();
            petEntity.setId(petDTO.getId());
            petEntity.setName(petDTO.getName());
            petEntity.setBirthDate(petDTO.getBirthDate());
            petEntity.setType(petDTO.getType());
            petEntity.setNotes(petDTO.getNotes());
            CustomerEntity customerEntity = customerRepository.findById(petDTO.getOwnerId()).orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + petDTO.getOwnerId()));
            petEntity.setCustomerEntity(customerEntity);
            petEntity = petRepository.save(petEntity);
            if(customerEntity.getPetEntities() == null){
                List<PetEntity> petEntities = new ArrayList<>();
                petEntities.add(petEntity);
                customerEntity.setPetEntities(petEntities);
            }else {
                customerEntity.addPetEntities(petEntity);
            }
            customerRepository.save(customerEntity);
            return getPetDTO(petEntity);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get pet with petId
     * @param petId
     * @return
     */
    public PetDTO getPet(Long petId){
        try {
            return getPetDTO(petRepository.findById(petId).orElseThrow(()-> new EntityNotFoundException("Pet not found with id: " + petId)));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Get all pets
     * @return
     */
    public List<PetDTO> getPets(){
        List<PetEntity> petEntities = StreamSupport
                .stream(petRepository.findAll()
                        .spliterator(), false)
                .collect(Collectors.toList());
        return petEntities.stream()
                .map(this::getPetDTO)
                .collect(Collectors.toList());

    }

    /**
     * Get pets by customer Id
     * @param ownerId
     * @return
     */
    public List<PetDTO> getPetsByOwner(Long ownerId){
        try {
            CustomerEntity customerEntity = customerRepository.findById(ownerId).orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + ownerId));
            List<PetEntity> petEntities = customerEntity.getPetEntities();
            return petEntities.stream()
                    .map(this::getPetDTO)
                    .collect(Collectors.toList());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Convert pet entity into dto
     * @param petEntity
     * @return
     */
    public PetDTO getPetDTO(PetEntity petEntity){
        PetDTO petDTO = new PetDTO();
        petDTO.setId(petEntity.getId());
        petDTO.setName(petEntity.getName());
        petDTO.setType(petEntity.getType());
        petDTO.setNotes(petEntity.getNotes());
        petDTO.setBirthDate(petEntity.getBirthDate());
        petDTO.setOwnerId(petEntity.getCustomerEntity().getId());
        return petDTO;
    }
}
