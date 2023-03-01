package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.EmployeeEntity;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Save employee data to database
     * @param employeeDTO
     * @return
     */
    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO){
        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setId(employeeDTO.getId());
        employeeEntity.setName(employeeDTO.getName());
        employeeEntity.setSkills(employeeDTO.getSkills());
        employeeEntity.setDaysAvailable(employeeDTO.getDaysAvailable());
        employeeEntity = employeeRepository.save(employeeEntity);
        return getEmployeeDTO(employeeEntity);

    }

    /**
     * Get employee with employeeId
     * @param employeeId
     * @return
     */
    public EmployeeDTO getEmployee(Long employeeId){
        try {
            EmployeeEntity employeeEntity = employeeRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + employeeId));
            return getEmployeeDTO(employeeEntity);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * set employee availability
     * @param daysAvailable
     * @param employeeId
     */
    public void setAvailability(Set<DayOfWeek> daysAvailable,Long employeeId){
        try {
            EmployeeEntity employeeEntity = employeeRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + employeeId));
            employeeEntity.setDaysAvailable(daysAvailable);
            employeeRepository.save(employeeEntity);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * find employees for service
     * @param employeeRequestDTO
     * @return
     */
    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeRequestDTO){
        LocalDate desiredDate = employeeRequestDTO.getDate();
        DayOfWeek desiredDay = desiredDate.getDayOfWeek();
        Set<EmployeeSkill> desiredSkills = employeeRequestDTO.getSkills();
        List<EmployeeEntity> employeeEntities = StreamSupport
                        .stream(employeeRepository.findAll()
                        .spliterator(), false)
                        .collect(Collectors.toList());

        return employeeEntities.stream()
                .filter(employeeEntity -> employeeEntity.getDaysAvailable().contains(desiredDay) && employeeEntity.getSkills().containsAll(desiredSkills))
                .map(employeeEntity -> getEmployeeDTO(employeeEntity))
                .collect(Collectors.toList());
    }

    /**
     * convert employee entity to dto
     * @param employeeEntity
     * @return
     */
    public EmployeeDTO getEmployeeDTO(EmployeeEntity employeeEntity){
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employeeEntity.getId());
        employeeDTO.setName(employeeEntity.getName());
        employeeDTO.setSkills(employeeEntity.getSkills());
        employeeDTO.setDaysAvailable(employeeEntity.getDaysAvailable());
        return employeeDTO;
    }
}
