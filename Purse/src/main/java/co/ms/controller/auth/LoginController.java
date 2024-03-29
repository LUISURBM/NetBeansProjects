/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.ms.controller.auth;

/**
 *
 * @author ADMIN
 */
import co.ms.controller.UserpurseJpaController;
import co.ms.entity.Userpurse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "loginController")

public class LoginController {

//    @Autowired
    UserpurseJpaController userService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView showLogin(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("login", new Userpurse());
        return mav;
    }

    @RequestMapping(value = "/loginProcess", method = RequestMethod.POST)
    public ModelAndView loginProcess(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("login") Userpurse login) {
        ModelAndView mav = null;
        Userpurse user = userService.findUserpurse(login.getUsername(), login.getPassword());
        if (null != user) {
            mav = new ModelAndView("welcome");
            mav.addObject("firstname", user.getUsername());
        } else {
            mav = new ModelAndView("login");
            mav.addObject("message", "Verifique Usuario o Contrasena, por favor!!");
        }
        return mav;
    }
}
