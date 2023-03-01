package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.CustomerEntity;
import com.udacity.jdnd.course3.critter.entity.PetEntity;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PetRepository petRepository;

    public CustomerService(CustomerRepository customerRepository, PetRepository petRepository) {
        this.customerRepository = customerRepository;
        this.petRepository = petRepository;
    }

    /**
     * Save customer data in database
     * @param customerDTO
     * @return
     */
    public CustomerDTO saveCustomer(CustomerDTO customerDTO){
        try {
            CustomerEntity customerEntity = new CustomerEntity();
            customerEntity.setId(customerDTO.getId());
            customerEntity.setName(customerDTO.getName());
            customerEntity.setPhoneNumber(customerDTO.getPhoneNumber());
            customerEntity.setNotes(customerDTO.getNotes());
            customerEntity = customerRepository.save(customerEntity);

            return getCustomerDTO(customerEntity);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all customer data
     * @return
     */
    public List<CustomerDTO> getAllCustomers(){
         List<CustomerEntity> customerEntities = StreamSupport
                 .stream(customerRepository.findAll()
                         .spliterator(), false)
                .collect(Collectors.toList());
         return customerEntities.stream()
                 .map(this::getCustomerDTO)
                 .collect(Collectors.toList());
    }

    /**
     * Get owner by pet id
     * @param petId
     * @return
     */
    public CustomerDTO getOwnerByPet(Long petId){
        try {
            PetEntity petEntity = petRepository.findById(petId).orElseThrow(() -> new EntityNotFoundException("Pet not found with id: " + petId));
            CustomerEntity customerEntity = petEntity.getCustomerEntity();
            if (customerEntity == null) {
                throw new EntityNotFoundException("Owner not found for pet with id: " + petId);
            }
            return getCustomerDTO(customerEntity);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method for converting customer entity to customer dto
     * @param customerEntity
     * @return
     */
    public CustomerDTO getCustomerDTO(CustomerEntity customerEntity){
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customerEntity.getId());
        customerDTO.setName(customerEntity.getName());
        customerDTO.setPhoneNumber(customerEntity.getPhoneNumber());
        customerDTO.setNotes(customerEntity.getNotes());
        if(customerEntity.getPetEntities() != null) {
            customerDTO.setPetIds(customerEntity.getPetEntities()
                    .stream()
                    .map(PetEntity::getId)
                    .collect(Collectors.toList()));
        }
        return customerDTO;
    }


}
