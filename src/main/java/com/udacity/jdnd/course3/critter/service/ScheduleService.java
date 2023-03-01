package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.CustomerEntity;
import com.udacity.jdnd.course3.critter.entity.EmployeeEntity;
import com.udacity.jdnd.course3.critter.entity.PetEntity;
import com.udacity.jdnd.course3.critter.entity.ScheduleEntity;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
public class ScheduleService{
        private final ScheduleRepository scheduleRepository;
        private  final CustomerRepository customerRepository;

        private final EmployeeRepository employeeRepository;

        private final PetRepository petRepository;

        public ScheduleService(ScheduleRepository scheduleRepository, CustomerRepository customerRepository, EmployeeRepository employeeRepository, PetRepository petRepository) {
                this.scheduleRepository = scheduleRepository;
                this.customerRepository = customerRepository;
                this.employeeRepository = employeeRepository;
                this.petRepository = petRepository;
        }


        /**
         * Save schedule in database
         * @param scheduleDTO
         * @return
         */
        public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
                try {
                        ScheduleEntity scheduleEntity = new ScheduleEntity();
                        scheduleEntity.setId(scheduleDTO.getId());
                        scheduleEntity.setActivities(scheduleDTO.getActivities());
                        scheduleEntity.setDate(scheduleDTO.getDate());
                        scheduleEntity.setEmployeeEntities(scheduleDTO.getEmployeeIds().stream()
                                .map(employeeId -> employeeRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + employeeId)))
                                .collect(Collectors.toList()));
                        scheduleEntity.setPetEntities(scheduleDTO.getPetIds().stream()
                                .map(petId -> petRepository.findById(petId).orElseThrow(()->new EntityNotFoundException("Pet not found with id: " + petId)))
                                .collect(Collectors.toList()));
                        scheduleEntity = scheduleRepository.save(scheduleEntity);
                        return getScheduleDTO(scheduleEntity);
                }catch(Exception e){
                        e.printStackTrace();
                        return null;
                }
        }

        /**
         * Get all schedules
         * @return
         */
        public List<ScheduleDTO> getAllSchedules(){
                List<ScheduleEntity> scheduleEntities = StreamSupport
                                .stream(scheduleRepository.findAll()
                                .spliterator(), false)
                                .collect(Collectors.toList());
                return scheduleEntities.stream()
                        .map(this::getScheduleDTO)
                        .collect(Collectors.toList());
        }

        /**
         * Get schedules for pet
         * @param petId
         * @return
         */
        public List<ScheduleDTO> getScheduleForPet(Long petId){
                List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
                List<ScheduleEntity> scheduleEntities = StreamSupport
                        .stream(scheduleRepository.findAll()
                                .spliterator(), false)
                        .collect(Collectors.toList());

                scheduleEntities.forEach(scheduleEntity -> {
                        List<PetEntity> petEntities = scheduleEntity.getPetEntities();
                        petEntities.forEach(petEntity -> {
                                if(petEntity.getId() == petId){
                                        scheduleDTOS.add(getScheduleDTO(scheduleEntity));
                                }
                        });
                });

        return scheduleDTOS;
        }

        /**
         * Get schedule for employee
         * @param employeeId
         * @return
         */
        public List<ScheduleDTO> getScheduleForEmployee(Long employeeId) {
                List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
                List<ScheduleEntity> scheduleEntities = StreamSupport
                        .stream(scheduleRepository.findAll()
                                .spliterator(), false)
                        .collect(Collectors.toList());

                scheduleEntities.forEach(scheduleEntity -> {
                        List<EmployeeEntity> employeeEntities = scheduleEntity.getEmployeeEntities();
                        employeeEntities.forEach(employeeEntity -> {
                                if(employeeEntity.getId() == employeeId){
                                        scheduleDTOS.add(getScheduleDTO(scheduleEntity));
                                }
                        });
                });

                return scheduleDTOS;
        }

        /**
         * get schedule for customer
         * @param customerId
         * @return
         */
        public List<ScheduleDTO> getScheduleForCustomer(Long customerId) {
                try {
                        CustomerEntity customerEntity = customerRepository.findById(customerId).orElseThrow(()-> new EntityNotFoundException("Customer not found with id: " + customerId));

                        List<Long> petIds = customerEntity.getPetEntities().stream()
                                .map(petEntity -> petEntity.getId())
                                .collect(Collectors.toList());

                        Set<ScheduleDTO> scheduleDTOSet = new HashSet<>();

                        petIds.stream()
                                .forEach(petId -> getScheduleForPet(petId).forEach(scheduleDTO -> scheduleDTOSet.add(scheduleDTO)));

                        return new ArrayList<>(scheduleDTOSet);
                }catch (Exception e){
                        e.printStackTrace();
                        return null;
                }

        }

        /**
         * Convert schedule entity to dto
         * @param scheduleEntity
         * @return
         */
        public ScheduleDTO getScheduleDTO(ScheduleEntity scheduleEntity){
                ScheduleDTO scheduleDTO = new ScheduleDTO();
                scheduleDTO.setId(scheduleEntity.getId());
                scheduleDTO.setActivities(scheduleEntity.getActivities());
                scheduleDTO.setDate(scheduleEntity.getDate());
                scheduleDTO.setEmployeeIds(scheduleEntity.getEmployeeEntities().stream()
                        .map(employeeEntity -> employeeEntity.getId())
                        .collect(Collectors.toList()));
                scheduleDTO.setPetIds(scheduleEntity.getPetEntities().stream()
                        .map(petEntity -> petEntity.getId())
                        .collect(Collectors.toList()));
                return scheduleDTO;
        }



}
