package com.playground.api.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playground.api.dto.LeaveDto;
import com.playground.api.dto.ResponseDto;
import com.playground.api.enums.LeaveEnum;
import com.playground.api.enums.RecordStatus;
import com.playground.api.model.Employee;
import com.playground.api.model.Leave;
import com.playground.api.repositories.EmployeeRepository;
import com.playground.api.repositories.LeaveRepository;

@RestController
@RequestMapping("/api/leave")
@CrossOrigin(origins = {"http://localhost:4200"})
public class LeaveController {
	
	@Autowired
	private LeaveRepository leaveRepository; 
	@Autowired
	private EmployeeRepository employeeRepository; 
	@Autowired
	private ResponseDto responseDto; 
	@PostMapping("/add")
	public Leave addLeave(@RequestBody LeaveDto leaveDto, Principal principal) {
		String username = principal.getName();
		Employee employee = employeeRepository.getByEmail(username);
		
		Leave leave = new Leave();
		leave.setFrom(leaveDto.getFrom());
		leave.setTo(leaveDto.getTo());
		leave.setDays(leaveDto.getDays());
		leave.setEmployee(employee);
		leave.setStatus(LeaveEnum.PENDING);
		leave.setRecordStatus(RecordStatus.ACTIVE);
		leave = leaveRepository.save(leave);
		 
		return leave;
	}
	
	@GetMapping("/employee/all/{status}")
	public List<LeaveDto> getAllLeaves(@PathVariable("status") String status, Principal principal) {
		String username = principal.getName();
		Employee employee = employeeRepository.getByEmail(username);
		LeaveEnum statusVal = LeaveEnum.valueOf(status);
		List<Leave> list = 
				leaveRepository.getLeavesByEmployeeUsername(employee.getUser().getUsername(),statusVal, RecordStatus.ACTIVE);
		List<LeaveDto> listDto = LeaveDto.convertToDto(list);
		return listDto;
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseDto> deleteLeaveSoft(@PathVariable("id") Long id) {
			Optional<Leave> optional = leaveRepository.findById(id);
			if(!optional.isPresent()) {
				responseDto.setMsg("Invalid Leave ID");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
			}
			Leave leave = optional.get();
			leave.setRecordStatus(RecordStatus.DELETED);
			leaveRepository.save(leave);
			responseDto.setMsg("Record Archived");
			return ResponseEntity.status(HttpStatus.OK).body(responseDto);

	}
}