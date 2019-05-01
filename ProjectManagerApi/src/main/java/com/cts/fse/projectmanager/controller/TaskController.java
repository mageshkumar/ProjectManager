package com.cts.fse.projectmanager.controller;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cts.fse.projectmanager.bean.TaskBean;
import com.cts.fse.projectmanager.service.TaskService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController()
@RequestMapping("projectmanager/api/task")
@CrossOrigin
public class TaskController {

	@Autowired
	TaskService service;

	@GetMapping()
	public List<TaskBean> retrieveAll() {
		log.info("retrieveAll");
		return service.findAll();
	}

	@PostMapping()
	public TaskBean add(@RequestBody TaskBean task) {
		log.info("Saving");
		return service.add(task);
	}

	@PutMapping()
	public TaskBean update(@RequestBody TaskBean task) {
		log.info("updating " + task);
		return service.update(task);
	}

	@GetMapping("/{id}")
	public TaskBean retrieveById(@PathVariable Long id) {
		log.info("retrive By Id : " + id);
		try {
			TaskBean task = service.findById(id);
			return task;
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found", ex);
		}
	}
	
	@GetMapping("retrieveByProjectId/{id}")
	public List<TaskBean> retrieveByProjectId(@PathVariable Long id) {
		log.info("retrive By Id : " + id);
		try {
			return service.findByProjectId(id);
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found", ex);
		}
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		log.info("Delete  with ID " + id);
		service.delete(id);
	}
}
