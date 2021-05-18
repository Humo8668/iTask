package uz.springBoot.iTask.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController
{
    @RequestMapping("/sayhello")
    public String SayHello()
    {
        return "Hello world";
    }

}
