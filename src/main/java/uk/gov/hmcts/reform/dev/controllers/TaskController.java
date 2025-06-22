package uk.gov.hmcts.reform.dev.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;


    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<List<Task>> getTasks() {
        List<Task> tasks = taskRepository.findAll();

        return ok(tasks);

    }

    @GetMapping(value = "/{taskId}", produces = "application/json")
    public ResponseEntity<Task> getTask(@PathVariable long taskId) {
        Optional<Task> maybeTask = taskRepository.findById(taskId);
        if (maybeTask.isPresent()) {
            return ok(maybeTask.get());
        } else {
            return notFound().build();
        }

    }

    @PatchMapping(value = "/{taskId}")
    public ResponseEntity<Void> patchTask(@PathVariable long taskId, @RequestBody Task patchData) {
        Optional<Task> maybeTask = taskRepository.findById(taskId);
        if (!maybeTask.isPresent()) {
            return notFound().build();
        }
        Task task = maybeTask.get();

        if (patchData.getTitle() != null)
            task.setTitle(patchData.getTitle());

        if (patchData.getDescription() != null)
            task.setDescription(patchData.getDescription());

        if (patchData.getStatus() != null)
            task.setStatus(patchData.getStatus());

        if (patchData.getDueDate() != null)
            task.setDueDate(patchData.getDueDate());

        taskRepository.save(task);

        return noContent().build();
    }

    @PostMapping(value = "/", produces = "application/json")
    public ResponseEntity<Long> postTask(@RequestBody Task task) {
        // todo: do some checks

        taskRepository.save(task);

        URI taskUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("tasks/"+task.getId()).build().toUri();

        return created(taskUri).body(task.getId());
    }

    @DeleteMapping(value = "/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable long taskId) {
        if (!taskRepository.findById(taskId).isPresent()) {
            return notFound().build();
        }

        taskRepository.deleteById(taskId);

        return noContent().build();
    }



}
