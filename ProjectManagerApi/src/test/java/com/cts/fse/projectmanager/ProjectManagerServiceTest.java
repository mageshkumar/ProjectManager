package com.cts.fse.projectmanager;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.cts.fse.projectmanager.bean.ProjectBean;
import com.cts.fse.projectmanager.bean.TaskBean;
import com.cts.fse.projectmanager.bean.UserBean;
import com.cts.fse.projectmanager.service.ProjectService;
import com.cts.fse.projectmanager.service.TaskService;
import com.cts.fse.projectmanager.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProjectManagerApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProjectManagerServiceTest {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	private HttpMessageConverter<Object> mappingJackson2HttpMessageConverter;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ProjectService projectService;
	@Autowired
	private UserService userService;
	@Autowired
	private TaskService taskService;

	@SuppressWarnings("unchecked")
	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		this.mappingJackson2HttpMessageConverter = (HttpMessageConverter<Object>) Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().orElse(null);

		assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
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
				.priority(15).startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(5)).build());

		ProjectBean appManagement = this.projectService.add(ProjectBean.builder().project("Application management")
				.manager(muthu).priority(15).startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(15)).build());

		TaskBean createData = this.taskService.add(TaskBean.builder().project(dataManagement).isParentTask(true)
				.taskName("Create Data").priority(10).status("NEW").startDate(LocalDate.now())
				.endDate(LocalDate.now().plusDays(10)).user(ragaveena).build());

		this.taskService.add(TaskBean.builder().project(dataManagement).isParentTask(false)
				.parentTask(createData).taskName("Create Data").priority(10).status("NEW").startDate(LocalDate.now())
				.endDate(LocalDate.now().plusDays(10)).user(magesh).build());

		TaskBean createApp = this.taskService.add(TaskBean.builder().project(appManagement).isParentTask(true)
				.taskName("Create App").priority(10).status("NEW").startDate(LocalDate.now())
				.endDate(LocalDate.now().plusDays(10)).user(magesh).build());

		this.taskService.add(TaskBean.builder().project(appManagement).isParentTask(false)
				.parentTask(createApp).taskName("Update App").priority(10).status("NEW").startDate(LocalDate.now())
				.endDate(LocalDate.now().plusDays(10)).user(ragaveena).build());
	}

	@Test
	public void testRetriveAllUser() throws Exception {
		mockMvc.perform(get("/projectmanager/api/user")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(4)));
	}

	@Test
	@Ignore
	public void testCreate() throws Exception {

		this.mockMvc
				.perform(post("/projectmanager/api/user").contentType(contentType)
						.content(json(UserBean.builder().firstName("Alagaraja").lastName("Ramasubramanian").empId(Long.valueOf(10005))
								.build())))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.firstName", is("Alagaraja"))).andExpect(jsonPath("$.lastName", is("Ramasubramanian")))
				.andExpect(jsonPath("$.empId", is(10005)));
		UserBean user = this.userService.findByEmpId(Long.valueOf(10005));

		this.mockMvc
				.perform(post("/projectmanager/api/project").contentType(contentType)
						.content(json(ProjectBean.builder().project("User management").priority(15).manager(user)
								.startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(15)).build())))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.project", is("User management"))).andExpect(jsonPath("$.priority", is(15)))
				.andExpect(
						jsonPath("$.startDate", is(DateTimeFormatter.ofPattern("YYYY-MM-dd").format(LocalDate.now()))))
				.andExpect(jsonPath("$.endDate",
						is(DateTimeFormatter.ofPattern("YYYY-MM-dd").format(LocalDate.now().plusDays(15)))));
		ProjectBean project = this.projectService.findByProjectName("User management");

		this.mockMvc
				.perform(post("/projectmanager/api/task").contentType(contentType)
						.content(json(TaskBean.builder().project(project).isParentTask(false).taskName("Create Data")
								.priority(10).status("NEW").startDate(LocalDate.now())
								.endDate(LocalDate.now().plusDays(10)).user(user).build())))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.taskName", is("Create Data"))).andExpect(jsonPath("$.priority", is(10)))
				.andExpect(jsonPath("$.status", is("NEW"))).andExpect(jsonPath("$.isParentTask", is(false)))
				.andExpect(
						jsonPath("$.startDate", is(DateTimeFormatter.ofPattern("YYYY-MM-dd").format(LocalDate.now()))))
				.andExpect(jsonPath("$.endDate",
						is(DateTimeFormatter.ofPattern("YYYY-MM-dd").format(LocalDate.now().plusDays(10)))));
	}

	@Test
	public void testCreateUser() throws Exception {
		String jsonUser = json(
				UserBean.builder().firstName("Alagaraja").lastName("Ramasubramanian").empId(Long.valueOf(10005)).build());
		this.mockMvc.perform(post("/projectmanager/api/user").contentType(contentType).content(jsonUser))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.firstName", is("Alagaraja"))).andExpect(jsonPath("$.lastName", is("Ramasubramanian")))
				.andExpect(jsonPath("$.empId", is(10005)));
	}

	@Test
	public void testUpdateUser() throws Exception {
		UserBean user = this.userService
				.add(UserBean.builder().firstName("Tom").lastName("Kitambi").empId(Long.valueOf(10006)).build());
		user.setFirstName("Thomas");
		this.mockMvc.perform(put("/projectmanager/api/user").contentType(contentType).content(json(user)))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.firstName", is("Thomas")))
				.andExpect(jsonPath("$.lastName", is("Kitambi"))).andExpect(jsonPath("$.empId", is(10006)));
	}
	
	@Test
	public void testDeleteUser() throws Exception {
		UserBean user = this.userService
				.add(UserBean.builder().firstName("Mohan").lastName("Rengasamy").empId(Long.valueOf(10008)).build());
		
		this.mockMvc.perform(delete("/projectmanager/api/user/" + user.getId()))
				.andExpect(status().isOk());
		
		this.mockMvc.perform(get("/projectmanager/api/user/" + user.getId()))
		.andExpect(status().isNotFound());
	}

	@Test
	public void testCreateProject() throws Exception {
		String jsonProject = json(ProjectBean.builder().project("User management").priority(15).startDate(LocalDate.now())
				.endDate(LocalDate.now().plusDays(15)).build());
		this.mockMvc.perform(post("/projectmanager/api/project").contentType(contentType).content(jsonProject))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.project", is("User management"))).andExpect(jsonPath("$.priority", is(15)))
				.andExpect(
						jsonPath("$.startDate", is(DateTimeFormatter.ofPattern("YYYY-MM-dd").format(LocalDate.now()))))
				.andExpect(jsonPath("$.endDate",
						is(DateTimeFormatter.ofPattern("YYYY-MM-dd").format(LocalDate.now().plusDays(15)))));
	}

	@Test
	public void testUpdateProject() throws Exception {
		ProjectBean project = this.projectService.add(ProjectBean.builder().project("Infra Management").priority(15)
				.startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(15)).build());
		project.setProject("Infrastructure Management");
		project.setPriority(30);
		project.setStartDate(LocalDate.now().plusDays(5));
		project.setEndDate(LocalDate.now().plusDays(30));
		this.mockMvc.perform(put("/projectmanager/api/project").contentType(contentType).content(json(project)))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.project", is("Infrastructure Management")))
				.andExpect(jsonPath("$.priority", is(30)))
				.andExpect(jsonPath("$.startDate",
						is(DateTimeFormatter.ofPattern("YYYY-MM-dd").format(LocalDate.now().plusDays(5)))))
				.andExpect(jsonPath("$.endDate",
						is(DateTimeFormatter.ofPattern("YYYY-MM-dd").format(LocalDate.now().plusDays(30)))));
	}
	
	@Test
	public void testDeleteProject() throws Exception {
		ProjectBean project = this.projectService.add(ProjectBean.builder().project("Network Management").priority(15)
				.startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(15)).build());
		
		this.mockMvc.perform(delete("/projectmanager/api/project/" + project.getId()))
				.andExpect(status().isOk());
		
		this.mockMvc.perform(get("/projectmanager/api/project/" + project.getId()))
		.andExpect(status().isNotFound());
	}

	@Test
	public void testCreateTask() throws Exception {
		String jsonTask = json(TaskBean.builder().isParentTask(false).taskName("Create Data").priority(10).status("NEW")
				.startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).build());
		this.mockMvc.perform(post("/projectmanager/api/task").contentType(contentType).content(jsonTask))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.taskName", is("Create Data"))).andExpect(jsonPath("$.status", is("NEW")))
				.andExpect(jsonPath("$.priority", is(10)))
				.andExpect(
						jsonPath("$.startDate", is(DateTimeFormatter.ofPattern("YYYY-MM-dd").format(LocalDate.now()))))
				.andExpect(jsonPath("$.endDate",
						is(DateTimeFormatter.ofPattern("YYYY-MM-dd").format(LocalDate.now().plusDays(10)))));
	}
	
	@Test
	public void testUpdateTask() throws Exception {
		TaskBean task = this.taskService.add(TaskBean.builder().isParentTask(false).taskName("Create Data").priority(10).status("NEW")
				.startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).build());
		task.setTaskName("Modify Data");
		task.setStatus("Completed");
		this.mockMvc.perform(put("/projectmanager/api/task").contentType(contentType).content(json(task)))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.taskName", is("Modify Data"))).andExpect(jsonPath("$.status", is("Completed")))
				.andExpect(jsonPath("$.priority", is(10)))
				.andExpect(
						jsonPath("$.startDate", is(DateTimeFormatter.ofPattern("YYYY-MM-dd").format(LocalDate.now()))))
				.andExpect(jsonPath("$.endDate",
						is(DateTimeFormatter.ofPattern("YYYY-MM-dd").format(LocalDate.now().plusDays(10)))));
	}
	
	@Test
	public void testDeleteTask() throws Exception {
		TaskBean task = this.taskService.add(TaskBean.builder().isParentTask(false).taskName("Create Data").priority(10).status("NEW")
				.startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).build());
		
		this.mockMvc.perform(delete("/projectmanager/api/task/" + task.getId()))
				.andExpect(status().isOk());
		
		this.mockMvc.perform(get("/projectmanager/api/task/" + task.getId()))
		.andExpect(status().isNotFound());
	}

	@Test
	public void testRetriveAllProject() throws Exception {
		mockMvc.perform(get("/projectmanager/api/project")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	public void testRetriveAllTask() throws Exception {
		mockMvc.perform(get("/projectmanager/api/task")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(4)));
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}
