/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.controller;

import com.encens.khipus.ejb.UsuarioFacade;
import com.encens.khipus.model.Usuario;
import com.encens.khipus.util.Encrypt;
import com.encens.khipus.util.JSFUtil;
import com.encens.khipus.util.ResourcesFiles;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Diego
 */
@Named(value = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L; 
    @EJB UsuarioFacade usuarioFacade;

    private Usuario usuario = new Usuario();
    @Inject
    ResourcesFiles rf;
   /* @Inject
    MenuBeans menuBeans; 
    @Inject 
    ValidadorRoles validadorRoles; */
 
    Boolean logeado = false; 
    /**  
     * Creates a new instance of LoginBean 
     */ 
     public Usuario getUsuario() {
        return usuario;
    } 
 
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    } 
 
    public Boolean getLogeado() { 
        return logeado; 
    } 
 
    public void setLogeado(Boolean logeado) { 
        this.logeado = logeado; 
    } 
 
    @PostConstruct
    public void init() { 
    } 
 
    public LoginBean() { 
    } 
   
    public String verificarLogin() { 

        try { 
            //menuBeans.habilitarTodo(false);
            setLogeado(Boolean.FALSE); 
            Usuario u = usuarioFacade.findByUserName(usuario.getUsuario());
            if(u == null){
                JSFUtil.addSuccessMessage("Usuario y/o contraseña son inválidos, intente nuevamente.");
                return "";
            }
            System.out.println("--->> Clave " + usuario.getClave() + ": " + Encrypt.sha512(usuario.getClave()));
            if (!u.getClave().equals(Encrypt.sha512(usuario.getClave()))) {
                JSFUtil.addSuccessMessage("Usuario y/o contraseña son inválidos, intente nuevamente.");
                return ""; 
            } 
            usuario = u;
            setLogeado(Boolean.TRUE);  
 
            return "";
 
        } catch (Exception e) { 
            JSFUtil.addErrorMessage(e, "verificarLogin()");  
        } 
 
        return null; 
    } 
 
    public String logout() { 
        try { 
 
            HttpSession session = (HttpSession)
FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            if (session != null) { 
                session.invalidate(); 
            } 
 String url = "/khipus/faces/index.xhtml?faces­redirect=true";
            FacesContext fc = FacesContext.getCurrentInstance(); 
            ExternalContext ec = fc.getExternalContext();
            try { 
                ec.redirect(url);  
            } catch (IOException ex) {
                JSFUtil.addErrorMessage(ex.getLocalizedMessage());  
            } 
 return "/khipus/faces/index.xhtml?faces­redirect=true";
        } catch (Exception e) { 
            JSFUtil.addErrorMessage(e, "logout()"); 
        } 
        return null; 
    } 
 
    public String irLogin() {  
        return "/index";  
    } 
 
    public void irInicio() { 
        FacesContext ctx = FacesContext.getCurrentInstance(); 
        ExternalContext extContext = ctx.getExternalContext(); 
 
        String url = 
extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx,  
"/index.xhtml")); 
 
        try { 
            extContext.redirect(url); 
        } catch (IOException ioe) {  
            JSFUtil.addErrorMessage(ioe.getLocalizedMessage().toString()); 
        } 
    }

    
}
