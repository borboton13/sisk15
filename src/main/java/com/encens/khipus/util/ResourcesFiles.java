/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.util;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

/**
 *
 * @author Diego
 */
@Named(value = "resourcesFiles")
@SessionScoped
public class ResourcesFiles implements Serializable {

   private static final long serialVersionUID = 1L;
	Locale currentLocale;
	ResourceBundle mrb; //for messages 
	ResourceBundle arb; //for application 


	public  ResourcesFiles() {
	}

	@PostConstruct
	public void init() {
		saveLocale();
	}

	public void saveLocale() {
		currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		mrb = ResourceBundle.getBundle("com.encens.khipus.properties.messages",
				currentLocale);
		arb = ResourceBundle.getBundle("com.encens.khipus.properties.application",
				currentLocale);

	}
	public Locale getCurrentLocale() {
		return currentLocale;
	}

	public void setCurrentLocale(Locale currentLocale) {
		this.currentLocale = currentLocale;
	}

	public ResourceBundle getMrb() {
		return mrb;
	}

	public void setMrb(ResourceBundle mrb) {
		this.mrb = mrb;
	}

	public ResourceBundle getArb() {
		return arb;
	}

	public void setArb(ResourceBundle arb) {
		this.arb = arb;
	} 
 
 /* 
     *Devuelve el mensaje Mrb 
     */

	public String getMensajeMrb(String mensaje) {
		return mrb.getString(mensaje);
	} 
    /* 
     *Devuelve el mensaje Arb 
     */

	public String getMensajeArb(String mensaje) {
		return arb.getString(mensaje);
	}
}
