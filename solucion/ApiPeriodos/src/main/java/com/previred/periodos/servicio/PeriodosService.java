package com.previred.periodos.servicio;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.previred.periodos.swagger.codegen.model.Periodo;

import jdk.nashorn.internal.parser.JSONParser;

/**
 *
 * @author mgonzalez@previred.com
 */
@Service
public class PeriodosService {

	private final static int MIN = 90;
	private final static int MAX = 100;

	/**
	 * Genera un Objetos periodos, los rangos de fechas van de 1980 a 2019 el rango
	 * de lista de fechas en el periodo va desde 90 a 100
	 *
	 * @return
	 */
	@Autowired
	private RestTemplate restTemplate;

	private String retornaFechasFaltantes(String fechaInicio, String fechaFin, String[] arrayFechas) {
		String FechasFaltantes = "";
		return FechasFaltantes;
	}

	public List<LocalDate> getListaEntreFechas(Date fechaInicio, Date fechaFin, String[] arrayFechasInformadas) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		boolean fechaExiste = true;
		String[] arrayFechasListadas;
		String date = null;
		String fechasFaltantes = "";
		String FechaInformada = "";
		String FechaListada = "";

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(fechaInicio);
		c2.setTime(fechaFin);
		List<String> listaFechasListadas = new ArrayList<String>();
		List<LocalDate> listaFechasFaltantes = new ArrayList<LocalDate>();
		// System.out.println("fechaInicio " + sdf.format( c1.getTime()) + " fechaFin "
		// + sdf.format( c2.getTime()) );
		while (!c1.after(c2)) {
			listaFechasListadas.add(sdf.format(c1.getTime()).trim());
			date = sdf.format(c1.getTime());
			c1.add(Calendar.MONTH, 1);
		}

		for (int si = 0; si < listaFechasListadas.size(); si++) {
			FechaListada = listaFechasListadas.get(si);

			for (int i = 0; i < arrayFechasInformadas.length; i++) {
				FechaInformada = arrayFechasInformadas[i];
				fechaExiste = false;

				if (FechaListada.trim().equals(FechaInformada.trim())) {
					fechaExiste = true;
					break;
				}
			}
			if (fechaExiste == false) {
				listaFechasFaltantes.add(LocalDate.parse(FechaListada));

				fechaExiste = false;
			}

		}

		return listaFechasFaltantes;
	}

	public ResponseEntity<Periodo> getPeriodos() {
	 
		String cadenaFechas = "";
		String url = "http://localhost:8080/periodos/api";
		ResponseEntity<Periodo> response = restTemplate.getForEntity(url, Periodo.class);
		Periodo p = new Periodo();
		p = response.getBody();
		cadenaFechas = p.getFechas().toString();
		cadenaFechas = cadenaFechas.toString().replace("[", "").toString().replace("]", "");
		String[] arrayFechas = cadenaFechas.split(",");
		Date D1, D2;
		D1 = Date.from(p.getFechaCreacion().atStartOfDay(ZoneId.systemDefault()).toInstant());
		D2 = Date.from(p.getFechaFin().atStartOfDay(ZoneId.systemDefault()).toInstant());	
		List<LocalDate> fechasFaltantes = null;
		fechasFaltantes = getListaEntreFechas(D1, D2, arrayFechas);		 
		p.setFechasFaltantes(fechasFaltantes);
		ResponseEntity<Periodo> respuesta = new ResponseEntity<>(p, HttpStatus.OK);
		return respuesta;
	}
}
