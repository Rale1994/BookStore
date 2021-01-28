package com.bookstore.controller;

import com.bookstore.domain.*;
import com.bookstore.security.PasswordResetToken;
import com.bookstore.security.Role;
import com.bookstore.security.UserRole;
import com.bookstore.service.BookService;
import com.bookstore.service.UserService;
import com.bookstore.serviceImpl.UserSecurityService;
import com.bookstore.utility.MailConstructor;
import com.bookstore.utility.SecurityUtility;
import com.bookstore.utility.USConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.*;

@Controller
public class HomeController {


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailConstructor mailConstructor;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSecurityService userSecurityService;

    @Autowired
    private BookService bookService;

    @RequestMapping("/")
    public String index() {
        return "home";
    }


    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("classActiveLogin", true);
        return "myAccount";
    }

    @RequestMapping("/forgetPassword")
    public String forgetPassword(HttpServletRequest request,
                                 Model model, @ModelAttribute("email") String userEmail) throws Exception {
        model.addAttribute("classActiveForgetPassword", true);

        User user = userService.findByEmail(userEmail);
        if (user == null) {
            model.addAttribute("emailNotExist", true);
            return "myAccount";
        }
        String password = SecurityUtility.randomPasswordGenerator();

        String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);

        user.setPassword(encryptedPassword);


        userService.saveUser(user);

        String token = UUID.randomUUID().toString();

        userService.createPasswordResetTokenForUser(user, token);

        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        SimpleMailMessage newEmail = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user, password);

        mailSender.send(newEmail);

        model.addAttribute("forgetPasswordEmailSent", "true");

        return "myAccount";
    }

    @RequestMapping(value = "/newUser", method = RequestMethod.POST)
    public String newUserPost(HttpServletRequest request, @ModelAttribute("email") String userEmail,
                              @ModelAttribute("username") String username,
                              Model model) throws Exception {
        model.addAttribute("classActiveNewAccount", true);
        model.addAttribute("email", userEmail);
        model.addAttribute("username", username);

        if (userService.findByUsername(username) != null) {
            model.addAttribute("usernameExist", true);
            return "myAccount";
        }
        if (userService.findByEmail(userEmail) != null) {
            model.addAttribute("emailExist", true);
            return "myAccount";
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(userEmail);

        String password = SecurityUtility.randomPasswordGenerator();

        String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);

        user.setPassword(encryptedPassword);

        Role role = new Role();
        role.setRoleId(1);
        role.setName("ROLE_USER");

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, role));

        userService.createUser(user, userRoles);

        String token = UUID.randomUUID().toString();

        userService.createPasswordResetTokenForUser(user, token);

        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        SimpleMailMessage email = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user, password);

        mailSender.send(email);

        model.addAttribute("emailSent", "true");

        return "myAccount";
    }


    @RequestMapping("/newUser")
    public String newUser(Locale locale, @RequestParam("token") String token, Model model) {
        PasswordResetToken passToken = userService.getPasswordResetToken(token);
        if (passToken == null) {
            String message = "Invalid token";
            model.addAttribute("message", message);
            return "redirect:/badRequest";
        }
        User user = passToken.getUser();
        String username = user.getUsername();

        UserDetails userDetails = userSecurityService.loadUserByUsername(username);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute("user", user);

        model.addAttribute("classActiveEdit", true);
        return "myProfile";
    }

    @RequestMapping("/bookshelf")
    public String bookShelf(Model model) {
        List<Book> bookList = bookService.findAll();
        model.addAttribute("bookList", bookList);
        return "bookShelf";

    }

    @RequestMapping("/bookDetail")
    public String bookDetail(
            @PathParam("id") Long id, Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            User user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }
        Book book = bookService.getOne(id);
        model.addAttribute("book", book);


        List<Integer> qtyList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        model.addAttribute("qtyList", qtyList);

        model.addAttribute("qty", 1);

        return "bookDetail";

    }

    @RequestMapping("/myProfile")
    public String myProfile(Model model, Principal principal) {

        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());

        //model.addAttribute("orderList", user.getOrderList);

        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("listOfShippingAddress", true);

        List<String> stateList = USConstants.listOfUSStatesCode;
        Collections.sort(stateList);
        model.addAttribute("stateList", stateList);
        model.addAttribute("classActiveEdit", true);

        return "myProfile";
    }

    @RequestMapping("/listOfCreditCards")
    public String listOfCreditCards(Model model, Principal principal, HttpServletRequest request) {
        User user = userService.findByUsername(principal.getName());

        model.addAttribute("user", user);
        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        /*model.addAttribute("orderList",user.getOrderList());  */
        return "myProfile";
    }

    @RequestMapping("/listOfShippingAddresses")
    public String listOfShippingAddresses(Model model, Principal principal, HttpServletRequest request) {
        User user = userService.findByUsername(principal.getName());

        model.addAttribute("user", user);
        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        /*model.addAttribute("orderList",user.getOrderList());  */

        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("classActiveBilling", true);
        model.addAttribute("listOfShippingAddresses", true);


        return "myProfile";
    }
    @RequestMapping("/addNewCreditCard")
    public String addNewCreditCard(
            Model model, Principal principal
    ){
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        model.addAttribute("addNewCreditCard", true);
        model.addAttribute("classActiveBilling", true);
        model.addAttribute("listOfShippingAddresses", true);

        UserBilling userBilling = new UserBilling();
        UserPayment userPayment = new UserPayment();


        model.addAttribute("userBilling", userBilling);
        model.addAttribute("userPayment", userPayment);

        List<String> stateList = USConstants.listOfUSStatesCode;
        Collections.sort(stateList);
        model.addAttribute("stateList", stateList);
        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        /*model.addAttribute("orderList", user.orderList());*/

        return "myProfile";
    }
    @RequestMapping("/addNewShippingAddress")
    public String addNewShippingAddress(
            Model model, Principal principal
    ){
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        model.addAttribute("addNewShippingAddress", true);
        model.addAttribute("classActiveShipping", true);

        UserShipping userShipping = new UserShipping();

        model.addAttribute("userShipping", userShipping);

        List<String> stateList = USConstants.listOfUSStatesCode;
        Collections.sort(stateList);
        model.addAttribute("stateList", stateList);
        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        /*model.addAttribute("orderList", user.orderList());*/

        return "myProfile";
    }


}
