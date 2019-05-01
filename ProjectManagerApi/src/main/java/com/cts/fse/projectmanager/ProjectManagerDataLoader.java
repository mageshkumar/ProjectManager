package com.cts.fse.projectmanager;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import com.cts.fse.projectmanager.bean.ProjectBean;
import com.cts.fse.projectmanager.bean.TaskBean;
import com.cts.fse.projectmanager.bean.UserBean;
import com.cts.fse.projectmanager.service.ProjectService;
import com.cts.fse.projectmanager.service.TaskService;
import com.cts.fse.projectmanager.service.UserService;

@Service
public class ProjectManagerDataLoader implements ApplicationRunner {

	@Autowired
	private ProjectService projectService;
	@Autowired
	private UserService userService;
	@Autowired
	private TaskService taskService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		this.taskService.deleteAll();
		this.projectService.deleteAll();
		this.userService.deleteAll();
		UserBean magesh = this.userService
				.add(UserBean.builder().firstName("Magesh").lastName("Kumar").empId(new Long(10001)).build());
		UserBean muthu = this.userService
				.add(UserBean.builder().firstName("Muthu").lastName("Kumar").empId(new Long(10002)).build());
		UserBean rajan = this.userService
				.add(UserBean.builder().firstName("Rajan").lastName("Sampath").empId(new Long(10003)).build());
		UserBean ragaveena = this.userService
				.add(UserBean.builder().firstName("Ragaveena").lastName("Kumar").empId(new Long(10004)).build());

		ProjectBean dataManagement = this.projectService.add(ProjectBean.builder().project("Data management").manager(rajan)
				.priority(15).startDate(LocalDate.now().minusDays(10)).endDate(LocalDate.now().plusDays(5)).build());

		ProjectBean appManagement = this.projectService.add(ProjectBean.builder().project("Application management")
				.manager(muthu).priority(15).startDate(LocalDate.now().minusDays(10)).endDate(LocalDate.now().plusDays(15)).build());

		TaskBean createData = this.taskService.add(TaskBean.builder().project(dataManagement).isParentTask(true).taskName("Create Data").priority(13)
				.status("NEW").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).user(ragaveena).build());
		
		this.taskService.add(TaskBean.builder().project(dataManagement).isParentTask(false).parentTask(createData).taskName("Update Data").priority(30)
				.status("NEW").startDate(LocalDate.now().minusDays(1)).endDate(LocalDate.now().plusDays(1)).user(magesh).build());

		TaskBean createApp = this.taskService.add(TaskBean.builder().project(appManagement).isParentTask(true).taskName("Create App").priority(15)
				.status("NEW").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(12)).user(magesh).build());
		
		this.taskService.add(TaskBean.builder().project(appManagement).isParentTask(false).parentTask(createApp).taskName("Update App").priority(20)
				.status("NEW").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(2)).user(ragaveena).build());
	}

}
