/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.tareas;
import icaro.aplicaciones.Rosace.informacion.PropuestaAgente;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.informacion.VictimsToRescue;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.informacion.InfoParaDecidirAQuienAsignarObjetivo;
import icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import icaro.infraestructura.entidadesBasicas.interfaces.InterfazUsoAgente;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.InformeDeTarea;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.TareaSincrona;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.ItfUsoRecursoTrazas;
import icaro.infraestructura.recursosOrganizacion.recursoTrazas.imp.componentes.InfoTraza;

import java.util.ArrayList;

/**
 *
 * @author Francisco J Garijo
 */
public class MandarPropuestaAlMejorParaQueRealiceObjetivo  extends TareaSincrona {

	/**  */
	private InterfazUsoAgente agenteReceptor;
	private ArrayList agentesEquipo, respuestasAgentes,confirmacionesAgentes,nuevasEvaluacionesAgentes,empates;//resto de agentes que forman mi equipo
	private String nombreAgenteEmisor;
	private ItfUsoRecursoTrazas trazas;
	private InfoParaDecidirAQuienAsignarObjetivo infoDecision;
	private String identDeEstaTarea ;
	private String nombreAgenteReceptor ;

	//private TimeOutRespuestas tiempoSinRecibirRespuesta; //no usado


	@Override
	public void ejecutar(Object... params) {
		try {
			trazas = NombresPredefinidos.RECURSO_TRAZAS_OBJ;
			Objetivo objetivoEjecutantedeTarea = (Objetivo)params[0];
			infoDecision = (InfoParaDecidirAQuienAsignarObjetivo)params[1];
			Victim victima = (Victim)params[2];
			MisObjetivos objs = (MisObjetivos)params[3];
			VictimsToRescue v2r = (VictimsToRescue)params[4];
			InformeDeTarea inf = (InformeDeTarea) params[5];
			nombreAgenteEmisor = this.getAgente().getIdentAgente();
			identDeEstaTarea = this.getIdentTarea();
			trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor, "Se Ejecuta la Tarea :"+ identDeEstaTarea , InfoTraza.NivelTraza.debug));
			//               if (! infoDecision.getheInformadoAlmejorParaQueAsumaElObjetivo() ){ // si ya se le ha informado no se hace nada
			nombreAgenteReceptor = infoDecision.dameIdentMejor();
			if(nombreAgenteReceptor!=null){
				//     PropuestaAgente miPropuesta = new PropuestaAgente (nombreAgenteEmisor,"CreoQueDebesIrTu", infoDecision.getValorMiEvaluacion());
				//                   this.getEnvioHechos().getStatefulKnowledgeSession().getAgenda().clear();
				PropuestaAgente miPropuesta = new PropuestaAgente (nombreAgenteEmisor);
				miPropuesta.setMensajePropuesta(VocabularioRosace.MsgPropuesta_Para_Aceptar_Objetivo);
				miPropuesta.setIdentObjectRefPropuesta(infoDecision.getidElementoDecision());
				miPropuesta.setJustificacion(victima);
				trazas.aceptaNuevaTrazaEjecReglas(identAgente, "Se asigna la victima: " + victima.getName() + " al agente " + nombreAgenteReceptor);
				this.getComunicator().enviarInfoAotroAgente(miPropuesta,nombreAgenteReceptor );
				this.generarInformeTemporizadoFromConfigProperty(VocabularioRosace.IdentTareaTimeOutRecibirConfirmacionesRealizacionObjetivo1,objetivoEjecutantedeTarea, 
						nombreAgenteEmisor,  infoDecision.getidElementoDecision());
				infoDecision.setheInformadoAlmejorParaQueAsumaElObjetivo(true);
				//objs.setVictimaAsignada(nombreAgenteReceptor, victima);
				this.getEnvioHechos().eliminarHechoWithoutFireRules(inf);
				this.getEnvioHechos().actualizarHecho(infoDecision);
				trazas.aceptaNuevaTraza(new InfoTraza(nombreAgenteEmisor,"IdentObjetoPropuesta: " +infoDecision.getidElementoDecision()+ "Enviamos la propuesta: " + VocabularioRosace.MsgPropuesta_Para_Aceptar_Objetivo + "  Al agente " +nombreAgenteReceptor  , InfoTraza.NivelTraza.debug));
			}
			else{
				infoDecision.setNoHayRobotAdecuado(true);
				//objs.setVictimaAsignada(nombreAgenteReceptor, victima);
				this.getEnvioHechos().actualizarHecho(infoDecision);
				v2r.addVictimNoAsignadas(victima);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
