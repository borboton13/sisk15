/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItem;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Diego
 */
@Named(value = "jSFUtil")
@RequestScoped
public class JSFUtil {

	/**
	 * Creates a new instance of JSFUtil
	 */
	public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
		int size = selectOne ? entities.size() + 1 : entities.size();
		SelectItem[] items = new SelectItem[size];
		int i = 0;
		if (selectOne) {
			items[0] = new SelectItem("", "​ ");
			i++;
		}
		for (Object x : entities) {
			items[i++] = new SelectItem(x, x.toString());
		}
		return items;
	} 
        /* 
     * logout 
     */


	public String logout() {
		HttpSession session = (HttpSession)
				FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return "/index";
	}

	public static void addErrorMessage(Exception ex, String defaultMsg) {
		String msg = ex.getLocalizedMessage();
		if (msg != null && msg.length() > 0) {
			addErrorMessage(msg);
		} else {
			addErrorMessage(defaultMsg);
		}
	}

	public static void addErrorMessages(List<String> messages) {
		for (String message : messages) {
			addErrorMessage(message);
		}
	}

	public static void addErrorMessage(String msg) {
		FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
				msg, msg);
		FacesContext.getCurrentInstance().addMessage(null, facesMsg);
	}

	public static void testMessage(String msg) {
		FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
				msg, msg);
		FacesContext.getCurrentInstance().addMessage(null, facesMsg);
	}

	public static void addSuccessMessage(String msg) {
		FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg,
				msg);
		FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
	}

	public static void addWarningMessage(String msg) {
		FacesContext.getCurrentInstance().addMessage(null, new
				FacesMessage(FacesMessage.SEVERITY_WARN, msg, ""));
	}

	public static void addFatalMessage(String msg) {
		FacesContext.getCurrentInstance().addMessage(null, new
				FacesMessage(FacesMessage.SEVERITY_FATAL, msg, ""));
	}

	public static String getRequestParameter(String key) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
	}

	public static Object getObjectFromRequestParameter(String requestParameterName,
													   Converter converter, UIComponent component) {
		String theId = JSFUtil.getRequestParameter(requestParameterName);
		return converter.getAsObject(FacesContext.getCurrentInstance(), component, theId);
	}

	public static void infoDialog(String titulo, String texto) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, titulo,
				texto);
		RequestContext.getCurrentInstance().showMessageInDialog(message);
	}

	public static void warningDialog(String titulo, String texto) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, titulo,
				texto);
		RequestContext.getCurrentInstance().showMessageInDialog(message);
	}

	public static void fatalDialog(String titulo, String texto) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, titulo,
				texto);
		RequestContext.getCurrentInstance().showMessageInDialog(message);
	}

	public static void errorDialog(String titulo, String texto) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
				titulo, texto);
		RequestContext.getCurrentInstance().showMessageInDialog(message);
	}

	public static java.sql.Date converterDate(java.util.Date fecha) {
		try {
			long lfecha = fecha.getTime();
			java.sql.Date dtfecha = new java.sql.Date(lfecha);
			return dtfecha;
		} catch (Exception e) {
			addErrorMessage("converterDate() " + e.getLocalizedMessage());
		}
		return null;
	}

	public static java.util.Date getFechaActual() {
		java.util.Calendar ca = java.util.Calendar.getInstance();
		java.sql.Date mydate = new java.sql.Date(ca.getTimeInMillis());
		return new java.sql.Date(mydate.getTime());

	}

	public static Integer getAnioActual() {
		java.util.Calendar ca = java.util.Calendar.getInstance();
		java.sql.Date mydate = new java.sql.Date(ca.getTimeInMillis());
		return ca.get(Calendar.YEAR);
	}

	public static Integer getMesActual() {
		java.util.Calendar ca = java.util.Calendar.getInstance();
		java.sql.Date mydate = new java.sql.Date(ca.getTimeInMillis());
		return ca.get(Calendar.MONTH);
	}

	public static Integer getMesDeUnaFecha(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int anio = calendar.get(Calendar.YEAR);
		int mes = calendar.get(Calendar.MONTH) + 1;
		int dia = calendar.get(Calendar.DAY_OF_MONTH);
		return mes;
	}

	public static Integer getAnioDeUnaFecha(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int anio = calendar.get(Calendar.YEAR);
		int mes = calendar.get(Calendar.MONTH) + 1;
		int dia = calendar.get(Calendar.DAY_OF_MONTH);
		return anio;
	}

	public static Integer getDiaDeUnaFecha(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int anio = calendar.get(Calendar.YEAR);
		int mes = calendar.get(Calendar.MONTH) + 1;
		int dia = calendar.get(Calendar.DAY_OF_MONTH);
		return dia;
	}

	public static Integer getDiaActual() {
		java.util.Calendar ca = java.util.Calendar.getInstance();
		java.sql.Date mydate = new java.sql.Date(ca.getTimeInMillis());
		return ca.get(Calendar.DATE);
	}

	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	public static ExternalContext getExternalContext() {
		return getFacesContext().getExternalContext();
	}

	public static ServletContext getServletContext() {
		return (ServletContext) getExternalContext().getContext();
	}

	public static String getRealPath(String url) {
		return getServletContext().getRealPath(url);
	}

	public static boolean isValidationFailed() {
		return FacesContext.getCurrentInstance().isValidationFailed();
	}

	public static boolean isDummySelectItem(UIComponent component, String value) {
		for (UIComponent children : component.getChildren()) {
			if (children instanceof UISelectItem) {
				UISelectItem item = (UISelectItem) children;
				if (item.getItemValue() == null && item.getItemLabel().equals(value)) {
					return true;
				}
				break;
			}
		}
		return false;
	}

	public static String getComponentMessages(String clientComponent, String defaultMessage) {
		FacesContext fc = FacesContext.getCurrentInstance();
		UIComponent component =
				UIComponent.getCurrentComponent(fc).findComponent(clientComponent);
		if (component instanceof UIInput) {
			UIInput inputComponent = (UIInput) component;
			if (inputComponent.isValid()) {
				return defaultMessage;
			} else {
				Iterator<FacesMessage> iter = fc.getMessages(inputComponent.getClientId());
				if (iter.hasNext()) {
					return iter.next().getDetail();
				}
			}
		}
		return "";
	}

	public static Throwable getRootCause(Throwable cause) {
		if (cause != null) {
			Throwable source = cause.getCause();
			if (source != null) {
				return getRootCause(source);
			} else {
				return cause;
			}
		}
		return null;
	}

	public static enum PersistAction {

		CREATE,
		DELETE,
		UPDATE
	}

}
