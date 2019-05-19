package com.projecty.projectyweb.controller;

import com.projecty.projectyweb.model.Project;
import com.projecty.projectyweb.model.Task;
import com.projecty.projectyweb.model.User;
import com.projecty.projectyweb.service.project.ProjectService;
import com.projecty.projectyweb.service.task.TaskService;
import com.projecty.projectyweb.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("project")
public class TaskController {

    @Autowired
    UserService userService;

    @Autowired
    ProjectService projectService;

    @Autowired
    TaskService taskService;

    @GetMapping("addtasks")
    public String addTasks(
            @RequestParam Long projectId, Model model
    ) {
        User current = userService.getCurrentUser();
        Optional<Project> project = projectService.findById(projectId);

        if (project.isPresent() && current.getProjects().contains(project.get())) {
            model.addAttribute("project", project.get());
            return "fragments/addtasks";
        }

        return "redirect:/404";
    }

    @PostMapping("addtasks")
    public RedirectView addTasksPost(
            @RequestParam Long projectId,
            @ModelAttribute Task task,
            BindingResult bindingResult
    ) {
        User current = userService.getCurrentUser();
        Optional<Project> project = projectService.findById(projectId);

        if (project.isPresent() && current.getProjects().contains(project.get())) {
            List<Task> tasks = project.get().getTasks();
            tasks.add(task);
            taskService.save(task);
        }

        RedirectView redirectView = new RedirectView("/project/myprojects");
        redirectView.setContextRelative(true);
        return redirectView;

    }

    @GetMapping("tasklist")
    public String taskList(
            @RequestParam Long projectId,
            Model model
    ) {
        Optional<Project> project = projectService.findById(projectId);
        if (project.isPresent() && projectService.isCurrentUserProjectUser(project.get())) {
            model.addAttribute("project", project.get());
            return "fragments/tasklist";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }


    }


}
