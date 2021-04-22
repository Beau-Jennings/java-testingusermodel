package com.lambdaschool.usermodel.services;

import com.lambdaschool.usermodel.UserModelApplicationTesting;
import com.lambdaschool.usermodel.exceptions.ResourceNotFoundException;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import com.lambdaschool.usermodel.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserModelApplicationTesting.class,
        properties =
                {
                        "command.line.runner.enabled=false"
                })
public class UserServiceImplUnitTestNoDB
{
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleService roleService;

    List<User> userList = new ArrayList<>();

    @Before
    public void setUp() throws Exception
    {
        Role r1 = new Role("admin");
        Role r2 = new Role("user");
        Role r3 = new Role("data");

        r1 = roleService.save(r1);
        r2 = roleService.save(r2);
        r3 = roleService.save(r3);

        // admin, data, user
        User u1 = new User("admin",
                "password",
                "admin@lambdaschool.local");
        u1.setUserid(10);
        u1.getRoles()
                .add(new UserRoles(u1,
                        r1));
        u1.getRoles()
                .add(new UserRoles(u1,
                        r2));
        u1.getRoles()
                .add(new UserRoles(u1,
                        r3));
        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@email.local"));
        u1.getUseremails().get(0).setUseremailid(11);
        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@mymail.local"));
        u1.getUseremails().get(1).setUseremailid(12);

        userList.add(u1);

        // data, user
        User u2 = new User("cinnamon",
                "1234567",
                "cinnamon@lambdaschool.local");
        u2.setUserid(20);
        u2.getRoles()
                .add(new UserRoles(u2,
                        r2));
        u2.getRoles()
                .add(new UserRoles(u2,
                        r3));
        u2.getUseremails()
                .add(new Useremail(u2,
                        "cinnamon@mymail.local"));
        u2.getUseremails().get(0).setUseremailid(21);
        u2.getUseremails()
                .add(new Useremail(u2,
                        "hops@mymail.local"));
        u2.getUseremails().get(1).setUseremailid(22);
        u2.getUseremails()
                .add(new Useremail(u2,
                        "bunny@email.local"));
        u2.getUseremails().get(2).setUseremailid(23);
        userList.add(u2);

        // user
        User u3 = new User("barnbarn",
                "ILuvM4th!",
                "barnbarn@lambdaschool.local");
        u3.setUserid(30);
        u3.getRoles()
                .add(new UserRoles(u3,
                        r2));
        u3.getUseremails()
                .add(new Useremail(u3,
                        "barnbarn@email.local"));
        u3.getUseremails().get(0).setUseremailid(31);
        userList.add(u3);

        User u4 = new User("puttat",
                "password",
                "puttat@school.lambda");
        u4.setUserid(40);
        u4.getRoles()
                .add(new UserRoles(u4,
                        r2));
        userList.add(u4);

        User u5 = new User("misskitty",
                "password",
                "misskitty@school.lambda");
        u4.setUserid(50);
        u5.getRoles()
                .add(new UserRoles(u5,
                        r2));
        userList.add(u5);

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void findUserById()
    {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(userList.get(0)));
        assertEquals("admin", userService.findUserById(1L).getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void findUserByIdNotFound()
    {
        Mockito.when(userRepository.findById(1337L))
                .thenReturn(Optional.empty());
        assertEquals("admin", userService.findUserById(1337L).getUsername());
    }

    @Test
    public void findByNameContaining()
    {
        Mockito.when(userRepository.findByUsernameContainingIgnoreCase("a"))
                .thenReturn(userList);
        assertEquals(5, userService.findByNameContaining("a").size());
    }

    @Test
    public void findByNameContainingNotFound()
    {
        Mockito.when(userRepository.findByUsernameContainingIgnoreCase("pwn"))
                .thenReturn(userList);
        assertEquals(5, userService.findByNameContaining("pwn").size());
    }

    @Test
    public void findAll()
    {
        Mockito.when(userRepository.findAll())
                .thenReturn(userList);
        assertEquals(5, userService.findAll().size());
    }

    @Test
    public void delete()
    {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(userList.get(0)));

        Mockito.doNothing()
                .when(userRepository)
                .deleteById(1L);

        userService.delete(1L);
        assertEquals(5, userList.size());
    }

    @Test
    public void findByName()
    {
        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(userList.get(0));
        assertEquals("admin",
                userService.findByName("admin").getUsername());
    }

    @Test
    public void save()
    {
        User u6 = new User("RandomCraig",
                "RandomPass",
                "RandomCraig@Randomemail.com");
        Role r4 = new Role("admin");
        u6.getRoles().add(new UserRoles(u6, r4));

        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(u6);

        User addUser = userService.save(u6);
        assertNotNull(addUser);
        assertEquals(u6.getUsername(), addUser.getUsername());
    }

    @Test
    public void update()
    {

    }

    @Test
    public void deleteAll()
    {

    }
}