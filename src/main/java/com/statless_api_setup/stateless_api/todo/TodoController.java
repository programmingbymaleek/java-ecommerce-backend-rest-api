package com.statless_api_setup.stateless_api.todo;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TodoController {
    static List<Todo> todos = new ArrayList<>();
    static {
        todos.add(new Todo("Wisdom", "Please plan an organized day schedule for work," +
                " school and company this week"));
    }
    @GetMapping(path = "/getAllTodos")
    public ResponseEntity<List<Todo>> getAllTodo() {
        return ResponseEntity.ok(todos);
    }

    @GetMapping(path = "/onlyAdmins")
    public ResponseEntity<String> messageForAdmins(){
        return ResponseEntity.ok("ahha, welcome to the admin section dude!!");
    }
}

record Todo(String name, String description) {
}
