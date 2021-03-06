package icaro.aplicaciones.Rosace.informacion;


import icaro.aplicaciones.Rosace.*;
import icaro.aplicaciones.Rosace.calculoRutas.AlgoritmoRuta;
import icaro.aplicaciones.Rosace.calculoRutas.Anterior;
import icaro.aplicaciones.Rosace.calculoRutas.AlgoritmoRutaLee;
import icaro.aplicaciones.Rosace.objetivosComunes.AyudarVictima;
import icaro.aplicaciones.agentes.agenteAplicacionAsignadorTareasCognitivo.objetivos.ReconocerTerreno;
import icaro.aplicaciones.agentes.agenteAplicacionrobotIgualitarioNCognitivo.informacion.InfoParaDecidirQuienVa;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.imp.MaquinaEstadoMovimientoCtrl;
import icaro.aplicaciones.recursos.recursoVisualizadorEntornosSimulacion.imp.EscenarioSimulacionRobtsVictms;
import icaro.aplicaciones.recursos.recursoVisualizadorEntornosSimulacion.imp.VisorEscenariosRosace;
import icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.MisObjetivos;
import icaro.infraestructura.entidadesBasicas.procesadorCognitivo.Objetivo;
import icaro.infraestructura.recursosOrganizacion.repositorioInterfaces.ItfUsoRepositorioInterfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.simpleframework.xml.core.Persister;


public class Coste {
	public static int tiempoAtencionVictima = 100;
	private static Coordinate lugarSeguro; 
	private double funcionEvaluacion=0;
	//    ItfUsoRecursoTrazas trazas = NombresPredefinidos.RECURSO_TRAZAS_OBJ; //Para depurar por la ventana de trazas de ICARO los calculos de costes

	//Constructor
	public Coste(){
		EscenarioSimulacionRobtsVictms escenario;
		try {
			escenario = new Persister().read(EscenarioSimulacionRobtsVictms.class,new File(NombresPredefinidos.RUTA_PERSISTENCIA_ESCENARIOS + VocabularioRosace.rutaEscenario),false);
			lugarSeguro = escenario.getCoordenadaLugarSeguro();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//Funcion de evaluacion que solo considera distancia entre la nueva victima y la posicion del robot. NO SE CONSIDERA LA ENERGIA NI LAS VICTIMAS QUE TIENE ASIGNADAS PREVIAMENTE.
	//En este caso, el tercer parametro, robot, solo se utiliza para la depuracion. No interviene en el calculo de la funcion de evaluacion de este metodo
	//El cuarto parametro solo se utiliza para la depuracion. No interviene en el calculo de la funcion de evaluacion de este metodo
	public double FuncionEvaluacion1(double par1DistanciaEntreDosPuntos, double pesoPar1, RobotStatus robot, Victim nuevaVictima){
		return pesoPar1 * par1DistanciaEntreDosPuntos;
	}	
	//Funcion de evaluacion que considera la energia disponible en el robot para intentar poder atender a las victimas que tenia y la nueva victima. 
	//El recorrido se haria de acuerdo a la prioridad de las victimas.  
	//El cuarto parametro, nuevaVictima, solo se utiliza para la depuracion. No interviene en el calculo de la funcion de evaluacion de este metodo	
	public double FuncionEvaluacion2(double par1DistanciaCamino, double pesoPar1, RobotStatus robot, Victim nuevaVictima){        
		if (par1DistanciaCamino > robot.getAvailableEnergy()){
			return -1.0 ;
		}
		else{
			return par1DistanciaCamino * pesoPar1;
		}
	}


	//Funcion de evaluacion que considera la ENERGIA disponible en el robot para intentar poder atender a las victimas que tenia y la nueva victima.
	//                   ademas considera el TIEMPO invertido en curar a cada victima
	//El recorrido se haria de acuerdo a la prioridad de las victimas.  
	//El cuarto parametro, nuevaVictima, solo se utiliza para la depuracion. No interviene en el calculo de la funcion de evaluacion de este metodo
	//En esta funcion FuncionEvaluacion3 SE ESTA SUPONIENDO QUE MIENTRAS EL ROBOT CURA A LA VICTIMA, EL ROBOT NO CONSUME ENERGIA
	public double FuncionEvaluacion3(double par1DistanciaCamino, double pesoPar1, double par2TiempoTotalAtencionVictimas, double pesoPar2, RobotStatus robot, Victim nuevaVictima){
		double resultado;
		//Si no tiene energía devuelve un -1 para indicar que no tiene recursos para ir
		if (par1DistanciaCamino > robot.getAvailableEnergy()){
			//		       trazas.aceptaNuevaTraza(new InfoTraza("Evaluacion", "Coste: FuncionEvaluacion3 sobre Victima(" + nuevaVictima.getName() + ")"  +
			//			          ": robot " + robot.getIdRobot() + "-> -1.0"
			//		    		   , InfoTraza.NivelTraza.info));       		        		                                                           		        		          		           			
			return -1.0 ;
		}
		else{
			resultado = (par1DistanciaCamino * pesoPar1) + (par2TiempoTotalAtencionVictimas*pesoPar2);
			//				trazas.aceptaNuevaTraza(new InfoTraza("Evaluacion", "Coste: FuncionEvaluacion3 sobre Victima(" + nuevaVictima.getName() + ")"  +
			//				       ": robot " + robot.getIdRobot() + "-> " + resultado
			//					   , InfoTraza.NivelTraza.info));								
			return resultado;
		}				
	}	


	//Funcion para desempatar. La nueva evaluacion es la evaluacion que tenia anteriormente + el identificador numerico del robot
	//                         Es decir, tendra una nueva evaluacion mayor, aquel robot que tenga mayor indice de entre los robots empatados.
	//ESTA FUNCION PRETENDE HACER LO MISMO QUE EL METODO calcularEvalucionParaDesempate DEFINIDO EN LA CLASE InfoParaDecidirQuienVa
	//SERIA MEJOR TENER TODOS LOS METODOS DE EVALUACIONES EN ESTA MISMA CLASE DE Coste
	//Actualmente FuncionEvaluacionDesempate no se usa en ningun otro sitio
	public int FuncionEvaluacionDesempate(InfoParaDecidirQuienVa infoDecision, RobotStatus robot){
		int evaluacionActual = infoDecision.mi_eval;  //recupero el valor de evaluacion que provoco el empate

		//ArrayList<String> agentesEmpatados = infoDecision.getAgentesEmpatados();

		if (evaluacionActual>=0){
			String identificadorRobot = robot.getIdRobot();

			//Obtener el identificador numerico del robot. Por ejemplo, para robotMasterIA3 la variable identficadorNumericoRobot tomara el valor 3
			int index = getNumberStartIndex(identificadorRobot);
			String number = getNumber(identificadorRobot,index);		
			int identficadorNumericoRobot = Integer.parseInt(number);

			infoDecision.tengoLaMejorEvaluacion = true;

			evaluacionActual = evaluacionActual + identficadorNumericoRobot;

			infoDecision.mi_eval = evaluacionActual; 

			return evaluacionActual;
		}
		else return evaluacionActual;		
	}
	//Calcula el tiempo que tardara en atender todas las victimas que tiene asignadas actualmente, mas el tiempo que tardara en atender a la nueva victima
	//El tiempo para atender una victima es igual al de la prioridad * factorMultiplicativo, siendo factorMultiplicativo el primer parametro pasado a este metodo

	public int CalculoCosteAyudarVictima (String nombreAgenteEmisor, Coordinate robotLocation,RobotStatus robot,Victim victima, VictimsToRescue victims2R, MisObjetivos misObjs, String identFuncEval){
		int aux;
		int energia=robot.getAvailableEnergy();
		if((aux = this.calculoRutaConVuelta(robotLocation, misObjs, victims2R, victima,energia)) != -1){
			funcionEvaluacion =  aux;
			return aux;
		}
		else{
			return Integer.MAX_VALUE;
		}
	}

	//Calcula el tiempo que tardara en atender todas las victimas que tiene asignadas actualmente, mas el tiempo que tardara en atender a la nueva victima
	//El tiempo para atender una victima es igual al de la prioridad * factorMultiplicativo, siendo factorMultiplicativo el primer parametro pasado a este metodo
	public double CalculaTiempoAtencion(double factorMultiplicativo, Victim nuevaVictima, VictimsToRescue victims2R, MisObjetivos misObjs){

		double tiempo = 0;     //Variable para calcular el tiempo

		//Obtener la prioridad de la victima
		int prioridadNuevaVictima = nuevaVictima.getPriority();
		// si la victima no esta entre las vicitimas a rescatar o en los objetivos

		PriorityBlockingQueue<Objetivo> colaobjetivos = misObjs.getMisObjetivosPriorizados();

		Iterator<Objetivo> it = colaobjetivos.iterator();
		boolean hayVictimasArescatar = victims2R.getvictims2Rescue().isEmpty();

		while (it.hasNext()&&hayVictimasArescatar){
			//Hay al menos un objetivo
			Objetivo ob = it.next();
			String referenciaIdObjetivo = ob.getobjectReferenceId();
			//Obtener la victima de la cola
			if (referenciaIdObjetivo !=null){
				Victim  victimaActualCola = victims2R.getVictimToRescue(referenciaIdObjetivo);
				if(victimaActualCola !=null){
					int prioridadVictimaActualCola = victimaActualCola.getPriority();
					tiempo = tiempo + (factorMultiplicativo*prioridadVictimaActualCola);
				}
			}
		}
		tiempo = tiempo + (factorMultiplicativo*prioridadNuevaVictima);
		return tiempo;
	}
	public int calculoRuta(Coordinate robotLocation, MisObjetivos misObjs, VictimsToRescue victims2Resc, Victim nuevaVictima,int energia){
		PriorityBlockingQueue<Objetivo> cola=misObjs.getMisObjetivosPriorizados();
		Iterator<Objetivo> it=cola.iterator();
		int time = 0;
		boolean[][] visitados= new boolean[VisorEscenariosRosace.ancho][VisorEscenariosRosace.alto];
		Coordinate actual = robotLocation;
		while(it.hasNext()){
			Objetivo x = it.next();
			if(x.getState()==Objetivo.SOLVING){
				String nombreVictima=x.getobjectReferenceId();
				Victim v=victims2Resc.getVictimToRescue(nombreVictima);
				visitados=matrizBooleanos(VisorEscenariosRosace.ancho,VisorEscenariosRosace.alto);
				visitados[(int)actual.getX()][(int)actual.getY()]=true;
				ArrayList<Coordinate> ruta=new ArrayList<Coordinate>();
				ruta.add(actual);
				ArrayList<Coordinate> arrayAux=new ArrayList<Coordinate>();
				try{
					AlgoritmoRuta alg=new AlgoritmoRuta(v.getCoordinateVictim(),actual);
					arrayAux = alg.calculaRuta(visitados, actual, Anterior.MOV_NULO, ruta);
				}
				catch(Exception e){
					System.out.println("");
				}

				if(arrayAux != null){
					time = time + arrayAux.size() + tiempoAtencionVictima;
					actual = v.getCoordinateVictim();
				}
				else return -1;
			}

		}

		visitados=matrizBooleanos(VisorEscenariosRosace.ancho,VisorEscenariosRosace.alto);

		visitados[(int)actual.getX()][(int)actual.getY()]=true;
		ArrayList<Coordinate> ruta=new ArrayList<Coordinate>();
		ruta.add(actual);
		AlgoritmoRuta alg2= new AlgoritmoRuta(nuevaVictima.getCoordinateVictim(), actual);
		ArrayList<Coordinate> arrayAux = alg2.calculaRuta(visitados, actual, Anterior.MOV_NULO,ruta);
		if(arrayAux != null){
			time += arrayAux.size();
			time += tiempoAtencionVictima;
			if(time<energia)
				return time;
		}
		return -1;
	}

	public int calculoRutaConVuelta(Coordinate robotLocation, MisObjetivos misObjs, VictimsToRescue victims2Resc, Victim nuevaVictima,int energia){
		PriorityBlockingQueue<Objetivo> cola=misObjs.getMisObjetivosPriorizados();
		Iterator<Objetivo> it=cola.iterator();
		int time = 0;
		Coordinate actual = robotLocation;
		while(it.hasNext()){
			Objetivo x = it.next();
			if(x.getState()==Objetivo.SOLVING){
				if(x instanceof AyudarVictima){
					String nombreVictima=x.getobjectReferenceId();
					Victim v=victims2Resc.getVictimToRescue(nombreVictima);
					ArrayList<Coordinate> viajeAVictima=new ArrayList<Coordinate>();
					ArrayList<Coordinate> viajeALugarSeguro=new ArrayList<Coordinate>();
					try{
						AlgoritmoRutaLee alg=new AlgoritmoRutaLee(v.getCoordinateVictim(),actual);
						viajeAVictima = alg.iniciarCalculoRuta();
						actual = new Coordinate(v.getCoordinateVictim().x,v.getCoordinateVictim().y,v.getCoordinateVictim().z);
						AlgoritmoRutaLee alg2=new AlgoritmoRutaLee(lugarSeguro,actual);
						viajeALugarSeguro=alg2.iniciarCalculoRuta();
						actual = new Coordinate(lugarSeguro.x,lugarSeguro.y,lugarSeguro.z);
					}
					catch(Exception e){
						System.out.println("");
					}
					if(viajeAVictima != null){
						time = time + viajeAVictima.size() + tiempoAtencionVictima;
						if(viajeALugarSeguro!=null)time +=viajeALugarSeguro.size();
					}
					else return -1;
				}
				else if(x instanceof ReconocerTerreno){
					int visionExplorador = 50;
					int alturaARecorrer = generarYFin(((ReconocerTerreno) x).getRobotId()),anchoMapa = 1100,recorridos = (alturaARecorrer - (int)(actual.getY()))/visionExplorador + 1;
					int tiempoRestanteExploracion = recorridos * anchoMapa + (alturaARecorrer - (int)(actual.getY()));
					time = time + tiempoRestanteExploracion;
					actual = new Coordinate(anchoMapa-visionExplorador,alturaARecorrer-visionExplorador,0.5);
				
				}
			}

		}
		Coordinate coordNuevaVictima = nuevaVictima.getCoordinateVictim();
		ArrayList<Coordinate> viajeAVictima=new ArrayList<Coordinate>();
		ArrayList<Coordinate> viajeALugarSeguro=new ArrayList<Coordinate>();
		AlgoritmoRutaLee alg3= new AlgoritmoRutaLee(nuevaVictima.getCoordinateVictim(), actual);
		viajeAVictima= alg3.iniciarCalculoRuta();
		actual = new Coordinate(coordNuevaVictima.x,coordNuevaVictima.y,coordNuevaVictima.z);
		AlgoritmoRutaLee alg4 = new AlgoritmoRutaLee(lugarSeguro,actual);
		viajeALugarSeguro = alg4.iniciarCalculoRuta();
		actual = new Coordinate(lugarSeguro.x,lugarSeguro.y,lugarSeguro.z);
		if(viajeAVictima != null){
			time += viajeAVictima.size();
			time += tiempoAtencionVictima;
			if(viajeALugarSeguro !=null)time += viajeALugarSeguro.size();
			if(time<energia)
				return time;
		}
		return -1;
	}
	
	private int generarYFin(int id){
		int finY = (700 * (id+1)) / VocabularioRosace.numeroReconocedores;
		if(finY == 700)
			return finY - 50;
		else return finY;
	}

	private boolean[][] matrizBooleanos(int ancho, int alto) {
		boolean [][] visitados=new boolean[ancho][alto];
		for(int i =0;i<ancho;i++)
			for(int j=0;j<alto;j++)
				visitados[i][j]=false;
		return visitados;
	}


	//Calcula la distancia del camino que pasa por las victimas actualmente asignadas, incluyendo la nueva victima actual que ha llegado.
	//El orden de visita de victimas esta determinado por las prioridades de las victimas, es decir, las de mayor prioridad se visitan primero.
	//Resaltar que cuandno hay varias victimas con la misma prioridad, se visitara primero aquella que lleva mas tiempo en la cola (mis objetivos).
	public double CalculaDistanciaCamino(String nombreAgenteEmisor, Coordinate posicionActualRobot, Victim nuevaVictima, VictimsToRescue victims2R, MisObjetivos misObjs){

		boolean flag = true;
		double distancia = 0;     //Variable para calcular la distancia para recorrer el camino de las victimas actualmente asignadas, incluyendo la nueva victima actual que ha llegado 

		Coordinate coordinateNuevaVictima = nuevaVictima.getCoordinateVictim();         
		int prioridadNuevaVictima = nuevaVictima.getPriority();
		Objetivo ob;
		PriorityBlockingQueue <Objetivo> colaobjetivos = misObjs.getMisObjetivosPriorizados();
		int tamaniocola = colaobjetivos.size();

		Victim victimaAnteriorCola = null;
		Coordinate coordinateVictimaAnteriorCola = null;
		// Hacer un analisis de casos 
		// Caso 1 Hay un objetivo en la cola de objetivos pero no tiene que ver con hacerse cargo de la victima
		//        victims2R contine la nueva victima que sera la ultima almacenada
		//        El coste debe consistir en el calculo a partir de la distancia      
		if (tamaniocola == 0 ) return  distanciaC1toC2(posicionActualRobot, coordinateNuevaVictima);
		else if (tamaniocola == 1){
			String refVictim = misObjs.getobjetivoMasPrioritario().getobjectReferenceId();
			if(refVictim == null||(victims2R.getvictims2Rescue().isEmpty() ||
					victims2R.getVictimToRescue(refVictim) == null )
					) return distanciaC1toC2(posicionActualRobot, coordinateNuevaVictima);  
		}

		Iterator<Objetivo> it = colaobjetivos.iterator();

		//Mostrar informacion del calculo de la evaluacion en ventana de trazas Evaluacion
		//trazas.aceptaNuevaTraza(new InfoTraza("Evaluacion", "CALCULANDO DISTANCIA .......... Agente " + nombreAgenteEmisor, InfoTraza.NivelTraza.info));

		int i = 1;

		while (it.hasNext()){
			//Hay al menos un objetivo
			ob = it.next();
			String referenciaIdObjetivo = ob.getobjectReferenceId();

			//    	      trazas.aceptaNuevaTraza(new InfoTraza("Evaluacion", "CalculaDistancia: Agente " + nombreAgenteEmisor + 
			//    	    		                                              ": Objetivo  " + i + "-> " + referenciaIdObjetivo 
			//    	      		                  , InfoTraza.NivelTraza.info));       		        		                                                           		        		          		           

			//Obtener la victima de la cola
			Victim victimaActualCola = victims2R.getVictimToRescue(referenciaIdObjetivo);    	          	      
			int prioridadVictimaActualCola = victimaActualCola.getPriority();    	          	          	      
			Coordinate coordinateVictimaActualCola = victimaActualCola.getCoordinateVictim();

			//    	      trazas.aceptaNuevaTraza(new InfoTraza("Evaluacion", "CalculaDistancia: Agente " + nombreAgenteEmisor + ": Victima->" + victimaActualCola
			//    	    		                  , InfoTraza.NivelTraza.info));       		        		                                                           		        		          		           

			if (flag == false){
				//terminar el recorrido por el resto de victimas de la cola
				distancia = distancia + distanciaC1toC2(coordinateVictimaAnteriorCola, coordinateVictimaActualCola);
			}
			else
			{     	      
				if (prioridadVictimaActualCola >= prioridadNuevaVictima){
					if (i==1){
						//distancia del robot a la nueva victima
						distancia = distanciaC1toC2(posicionActualRobot, coordinateVictimaActualCola);
					}
					else{
						distancia = distancia + distanciaC1toC2(coordinateVictimaAnteriorCola, coordinateVictimaActualCola); 
					}
					if (i==tamaniocola) 
						distancia = distancia + distanciaC1toC2(coordinateVictimaActualCola, coordinateNuevaVictima);
				}
				else //prioridadVictimaActualCola < prioridadNuevaVictima
				{
					if (i==1){
						distancia = distanciaC1toC2(posicionActualRobot, coordinateNuevaVictima);
					}
					else {
						distancia = distancia + distanciaC1toC2(coordinateVictimaAnteriorCola, coordinateNuevaVictima);
					}
					distancia = distancia + distanciaC1toC2(coordinateNuevaVictima, coordinateVictimaActualCola);
					flag = false;
				}
			}

			i = i + 1;
			victimaAnteriorCola = victimaActualCola;
			coordinateVictimaAnteriorCola = victimaAnteriorCola.getCoordinateVictim();
		} //fin del while

		//trazas.aceptaNuevaTraza(new InfoTraza("Evaluacion", 
		//		"CalculaDistancia: Agente " + nombreAgenteEmisor + ": Distancia->" + distancia 
		//		, InfoTraza.NivelTraza.info));       		        		                                                           		        		          		           

		return distancia;
	}    


	//Calcula la distancia entre dos puntos
	public static double distanciaC1toC2(Coordinate c1, Coordinate c2){

		//System.out.println("Coord calculo Coste c1->"+c1);
		//System.out.println("Coord calculo Coste c2->"+c2);

		return Math.sqrt( Math.pow(c1.x - c2.x,2) + 
				Math.pow(c1.y - c2.y,2) +
				Math.pow(c1.z - c2.z,2) 
				);       		               	
	}


	//Calcula la tiempo que tarda en recorrer el camino de todas las victimas de las que se ha hecho responsable.
	//El orden de visita de victimas esta determinado por las prioridades de las victimas, es decir, las de mayor prioridad se visitan primero.
	//Resaltar que cuando hay varias victimas con la misma prioridad, se visitara primero aquella que lleva mas tiempo en la cola (mis objetivos).
	public double calculaCosteRecorrerCaminoVictimas(String nombreAgenteEmisor, Coordinate posicionActualRobot, VictimsToRescue victims2R, MisObjetivos misObjs){    	
		double tiempoCamino = 0;     //Variable para calcular el tiempo para recorrer el camino de las victimas asignadas        
		double tiempoAux = 0;

		PriorityBlockingQueue <Objetivo> colaobjetivos = misObjs.getMisObjetivosPriorizados();
		int tamaniocola = colaobjetivos.size();

		Iterator<Objetivo> it = colaobjetivos.iterator();

		if (tamaniocola==0){
			return 0;
		}

		int i = 1;
		Coordinate coordinateaux = new Coordinate(0.0,0.0,0.0);
		coordinateaux = posicionActualRobot;

		while (it.hasNext()){
			//Hay al menos un objetivo    		
			Objetivo ob = it.next();
			String referenciaIdObjetivo = ob.getobjectReferenceId();

			//Obtener la victima de la cola
			Victim victimaActualCola = victims2R.getVictimToRescue(referenciaIdObjetivo);
			Coordinate coordinateVictimaActualCola = victimaActualCola.getCoordinateVictim();
			if (i==1){
				//distancia del robot a la nueva victima
				tiempoAux = tiempoAux + distanciaC1toC2(coordinateaux, coordinateVictimaActualCola);
				coordinateaux = coordinateVictimaActualCola;
				i=2;
			}
			else{
				//distancia de una victima a la siguiente victima a visitar
				tiempoAux = tiempoAux + distanciaC1toC2(coordinateaux, coordinateVictimaActualCola);
				coordinateaux = coordinateVictimaActualCola;
				i++;
			}	      	      
		}        
		System.out.println("Numero de victimas asignadas al robot " + nombreAgenteEmisor + " -> " + (i-1));    	
		tiempoCamino = tiempoAux;    	
		return tiempoCamino;    	
	}


	//Calcula el tiempo que tardara en atender todas las victimas que tiene asignadas
	//El tiempo para atender una victima es igual al de la prioridad * factorMultiplicativo, siendo factorMultiplicativo el primer parametro pasado a este metodo
	public double calculaCosteAtencionVictimasFinalesAsignadas(double factorMultiplicativo, VictimsToRescue victims2R, MisObjetivos misObjs){

		double tiempo = 0;     //Variable para calcular el tiempo

		PriorityBlockingQueue <Objetivo> colaobjetivos = misObjs.getMisObjetivosPriorizados();
		int tamaniocola = colaobjetivos.size();

		Iterator<Objetivo> it = colaobjetivos.iterator();

		if (tamaniocola==0){
			return 0;
		}

		while (it.hasNext()){
			//Hay al menos un objetivo
			Objetivo ob = it.next();
			String referenciaIdObjetivo = ob.getobjectReferenceId();

			//Obtener la victima de la cola
			Victim victimaActualCola = victims2R.getVictimToRescue(referenciaIdObjetivo);    	          	      
			int prioridadVictimaActualCola = victimaActualCola.getPriority();

			tiempo = tiempo + (factorMultiplicativo*prioridadVictimaActualCola);
		}    	    	    	
		return tiempo;
	}




	public double calculaCosteTotalCompletarMisionAtenderVictimasFinalesAsignadas(double par1TiempoRecorrerCaminoVictimasAsignadas, double pesoPar1, 
			double par2TiempoAtencionVictimasAsignadas, double pesoPar2){		
		double resultado;

		resultado = (par1TiempoRecorrerCaminoVictimasAsignadas * pesoPar1) + 
				(par2TiempoAtencionVictimasAsignadas*pesoPar2);

		return resultado;		
	}	






	//----------------------------------------------------
	//   METODOS UTILIDADES (NO EVALUAN COSTE)
	//----------------------------------------------------

	//El string finaliza en un numero.
	//Este metodo devuelve la posicion en el que empieza el numero.
	private int getNumberStartIndex(String s){    	
		int index=0;

		for (int x=s.length()-1;x>=0;x--){
			char ch = s.charAt(x);
			String sch = "" + ch;
			int chint = (int)ch;    		
			int numberchint = chint - 48; //48 es el valor ascii del 0
			if ((numberchint<0) || (numberchint >= 10)) //no es un numero
			{   
				return x+1;
			}                   		
		}
		return index;
	}


	//El string finaliza en un numero.
	//Este metodo devuelve el substring que contiene el numero    
	private String getNumber(String s, int index){
		String stringNumber;
		stringNumber = s.substring(index);    	    	
		return stringNumber;
	}



}
