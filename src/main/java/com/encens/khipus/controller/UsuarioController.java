/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.controller;

import com.encens.khipus.ejb.UsuarioFacade;
import com.encens.khipus.model.Usuario;
import com.encens.khipus.util.JSFUtil;
import com.encens.khipus.util.ResourcesFiles;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author Diego
 */
@Named(value = "usuarioController")
@RequestScoped
public class UsuarioController {
	@Inject
	UsuarioFacade usuarioFacade;
	Usuario usuario = new Usuario();
	private Boolean encontrado = false;
	private List<Usuario> items;
	@Inject
	ResourcesFiles rf;

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Boolean getEncontrado() {
		return encontrado;
	}

	public void setEncontrado(Boolean encontrado) {
		this.encontrado = encontrado;
	}

	/**
	 * Creates a new instance of UsuarioController  
	 */
	
	public UsuarioController() {
	}

	public String save() {
		try {

			/*if(usuario.getIdusuario() != null);
			if (usuarioFacade.find(usuario.getIdusuario()) != null) {
				JSFUtil.warningDialog(rf.getMensajeArb("info.message"),rf.getMensajeArb("warning.idexist"));
				return null;
			}*/
			usuarioFacade.create(usuario);
			JSFUtil.addSuccessMessage(rf.getMensajeArb("info.save"));
			usuario = new Usuario();
		} catch (Exception e) {
			JSFUtil.addErrorMessage(e.getLocalizedMessage());
		}
		return null;
	}

	public String edit() {
		try {
			usuarioFacade.edit(usuario);
			JSFUtil.addSuccessMessage(rf.getMensajeArb("info.update"));
		} catch (Exception e) {
			JSFUtil.addErrorMessage(e.getLocalizedMessage());
		}
		return null;
	}

	public String delete() {
		try {
			usuarioFacade.remove(usuario);
			JSFUtil.addSuccessMessage(rf.getMensajeArb("info.delete "));
			encontrado = false;
			usuario = new Usuario();

		} catch (Exception e) {
			JSFUtil.addErrorMessage(e.getLocalizedMessage());
		}
		return null;
	}
	//Devuelve el list para un selectOneMenu 
	public List<Usuario> getItems() {
		if (items == null) {
			items = usuarioFacade.findAll();
		}
		return items;
	}    
    
}
