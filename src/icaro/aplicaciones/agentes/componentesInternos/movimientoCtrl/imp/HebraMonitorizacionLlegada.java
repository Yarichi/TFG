
package icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.imp;


import java.util.ArrayList;

import org.apache.log4j.Logger;

import icaro.aplicaciones.Rosace.calculoRutas.AlgoritmoRuta;
import icaro.aplicaciones.Rosace.calculoRutas.Anterior;
import icaro.aplicaciones.Rosace.informacion.Coordinate;
import icaro.aplicaciones.Rosace.informacion.RobotStatus;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.Rosace.informacion.VocabularioRosace;
import icaro.aplicaciones.agentes.agenteAplicacionSubordinadoConCambioRolCognitivo.tareas.GeneraryEncolarObjetivoReconocerTerreno;
import icaro.aplicaciones.recursos.recursoVisualizadorEntornosSimulacion.ItfUsoRecursoVisualizadorEntornosSimulacion;
import icaro.aplicaciones.recursos.recursoVisualizadorEntornosSimulacion.imp.VisorEscenariosRosace;
import icaro.infraestructura.entidadesBasicas.comunicacion.Informacion;


/**
 * Clase interna que se encarga de generar eventos de monitorizacin cada cierto tiempo
 *
 * @author Carlos Delgado
 */
public class HebraMonitorizacionLlegada extends Thread {

	/**
	 * Milisegundos que esperar antes de lanzar otra monitorizacin
	 * @uml.property  name="milis"
	 */
	protected long milis;

	/**
	 * Indica cundo debe dejar de monitorizar
	 * @uml.property  name="finalizar"
	 */
	protected volatile boolean finalizar = false;

	/**
	 * Agente reactivo al que se pasan los eventos de monitorizacin
	 * @uml.property  name="agente"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	public MaquinaEstadoMovimientoCtrl controladorMovimiento;
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	/**
	 * Evento a producir
	 * @uml.property  name="evento"
	 */
	private String identDestino, identRobot;
	private   volatile Coordinate coordActuales;
	private volatile Coordinate coordDestino;
	private volatile Coordinate coordIncremento;
	private double velocidadRobot; // en metros por segundo 
	//     protected int intervaloEnvioInformacion = 1000; // por defecto en ms . Deberia ser configurable
	private volatile double pendienteRecta;
	private volatile boolean estamosEnDestino;
	private double espacioRecorrido ;
	//     protected double distanciaDestino ;
	private boolean pendienteInfinita = false ;
	private volatile boolean parar = false ;
	private volatile boolean enDestino = false ;
	private boolean energia = true;
	private float distanciaArecorrer ;
	private float b ; // punto corte recta con eje Y
	private int dirX =0, dirY=0,incrementoDistancia=0;
	private int intervaloEnvioInformesMs ;
	private int distanciaRecorridaEnIntervaloInformes ;
	private long tiempoParaAlcanzarDestino = 2000;
	private RobotStatus robotStatus;
	public ItfUsoRecursoVisualizadorEntornosSimulacion itfusoRecVisSimulador;


	private int contadorAuxiliar=0;

	//    private int numeroPuntos = 20;
	/**
	 * Constructor
	 *
	 * @param milis Milisegundos a esperar entre cada monitorizacion
	 */
	public HebraMonitorizacionLlegada(String idRobot,MaquinaEstadoMovimientoCtrl contrMovimiento,  ItfUsoRecursoVisualizadorEntornosSimulacion itfRecVisSimulador) {
		super("HebraMonitorizacion "+idRobot);  
		controladorMovimiento =contrMovimiento;
		this.itfusoRecVisSimulador = itfRecVisSimulador;
		identRobot = idRobot;
	}
	public HebraMonitorizacionLlegada(String idRobot,MaquinaEstadoMovimientoCtrl contrMovimiento,  ItfUsoRecursoVisualizadorEntornosSimulacion itfRecVisSimulador, RobotStatus robotStatus) {
		super("HebraMonitorizacion "+idRobot);  
		controladorMovimiento =contrMovimiento;
		this.itfusoRecVisSimulador = itfRecVisSimulador;
		identRobot = idRobot;
		this.robotStatus = robotStatus;
	}
	public synchronized void inicializarDestino (String idDestino,Coordinate coordRobot,Coordinate coordDest, double velocidad ){    
		//      this.finalizar= false;
		coordActuales =coordRobot ;
		coordDestino = coordDest;
		velocidadRobot = velocidad; // 
		//      intervaloEnvioInformacion= intervEnvioInformacion; 
		espacioRecorrido = 0;
		identDestino = idDestino;
		//      this. pendienteRecta = (float) ((coordDestino.y-coordActuales.y)/(coordDestino.x-coordActuales.x));
		log.debug ("Coord Robot " + identRobot + " iniciales -> ("+this.coordActuales.getX() + " , " + this.coordActuales.getY() + ")");
		log.debug ("Coord Robot " + identRobot + " destino -> ("+this.coordDestino.getX() + " , " + this.coordDestino.getY() + ")");
		this.setDaemon(true);
		//      coordIncremento = this.calcularIncrementosCoordenadasAvelocidadConstante(intervaloEnvioInformacion);
		//     this.evento = notificacionAProducir;
		this.finalizar= false;
		//       distanciaDestino = this.distanciaEuclidC1toC2(coordActuales, coordDestino);
		double incrX=(coordDestino.getX()-coordActuales.getX());
		double incrY=(coordDestino.getY()-coordActuales.getY());
		if (incrX>0)dirX=1 ;
		else dirX=-1;
		if (incrY>0)dirY=1 ;
		else dirY=-1;
		if (incrX==0 &&incrY!=0){pendienteInfinita= true;
		distanciaArecorrer = (float)incrY;
		}else if (incrX==0 &&incrY==0) finalizar=true;
		else { pendienteRecta = (float) Math.abs(incrY/incrX);
		this.distanciaArecorrer =(float) Math.sqrt(incrX*incrX+incrY*incrY);
		this.b = (float) (coordActuales.y -pendienteRecta * coordActuales.x ) ;

		}
		intervaloEnvioInformesMs = (int)(velocidadRobot* 12);
		distanciaRecorridaEnIntervaloInformes = 1;
	}

	public void pararRobot(){
		this.finalizar=true;
	}

	/**
	 * Termina la monitorizacin
	 */
	public synchronized void finalizar() {
		this.finalizar= true;
		this.notifyAll();

	}
	public synchronized void setCoordRobot(Coordinate robotCoord) {
		this.coordActuales= robotCoord;
	}
	public Coordinate getCoordRobot() {
		return coordActuales;
	}
	public synchronized void setCoordDestino(Coordinate destCoord) {
		this.coordDestino= destCoord;
	}
	public synchronized Coordinate getCoordDestino() {
		return coordDestino;
	}
	public synchronized void setVelocidadRobot(double velRobot) {
		this.velocidadRobot= velRobot;
	}

	@Override
	public synchronized void run() {
		int energiaActual=this.robotStatus.getAvailableEnergy();
		//       double espacioRecorridoEnIntervalo = velocidadRobot*intervaloEnvioInformacion;
		log.debug ("Coord Robot " + identRobot + " iniciales -> ("+this.coordActuales.getX() + " , " + this.coordActuales.getY() + ")");
		log.debug ("Coord Robot " + identRobot + " destino -> ("+this.coordDestino.getX() + " , " + this.coordDestino.getY() + ")");
		//       System.out.println("Coord Robot " + identRobot + " iniciales -> ("+this.coordActuales.x + " , " + this.coordActuales.y + ")");
		//      this.itfusoRecVisSimulador.mostrarMovimientoAdestino(identRobot,identDestino, coordActuales,velocidadRobot);
		while (!this.finalizar && (!enDestino)) {
			try {
				AlgoritmoRuta alg=new AlgoritmoRuta(this.coordDestino, this.coordActuales);
				ArrayList<Coordinate> ruta=new ArrayList<Coordinate>();
				ruta.add(coordActuales);
				alg.iniciarCalculoruta(coordActuales, ruta);
				int anchoVictima =15;
				int referenciaExploracion = (int)this.coordActuales.getX();
				if(ruta!=null){
					while(!enDestino && this.energia){
						for(int i=0;i<ruta.size() && !this.finalizar && this.energia ;i++){

							Coordinate punto=ruta.get(i);
							this.coordActuales.setY(punto.getY());
							this.coordActuales.setX(punto.getX());
							enDestino = ((coordActuales.getX()-coordDestino.getX())*dirX>=0 &&(coordActuales.getY()-coordDestino.getY())*dirY>=0);
							if (itfusoRecVisSimulador != null)
								this.itfusoRecVisSimulador.mostrarPosicionRobot(identRobot, coordActuales);
							this.controladorMovimiento.setCoordenadasActuales(coordActuales);
							/**
							 * Si estas explorando entonces se comprueba que en el perímetro de vision del robot haya victimas. 
							 */

							//Movimiento a derechas
							if(this.controladorMovimiento.estadoActual.getActuacion() == 1 && (referenciaExploracion + anchoVictima) == (int)this.coordActuales.getX() ){
								referenciaExploracion = referenciaExploracion + anchoVictima;
								final int perimetroDeVision = GeneraryEncolarObjetivoReconocerTerreno.perimetroDeVision;
								Thread t = new Thread(){
									public void run(){
										try {
											itfusoRecVisSimulador.comprobarVictimasArea(coordActuales, perimetroDeVision);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								};
								t.start();

							}

							//Movimiento a izquierdas
							else if(this.controladorMovimiento.estadoActual.getActuacion() == 1 && (referenciaExploracion - anchoVictima) == (int)this.coordActuales.getX()){
								referenciaExploracion = referenciaExploracion - anchoVictima;
								final int perimetroDeVision = GeneraryEncolarObjetivoReconocerTerreno.perimetroDeVision;
								Thread t = new Thread(){
									public void run(){
										try {
											itfusoRecVisSimulador.comprobarVictimasArea(coordActuales, perimetroDeVision);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								};
								t.start();

							}
							else Thread.sleep(intervaloEnvioInformesMs);


							if(energiaActual > 0){
								energiaActual--;
								this.robotStatus.setAvailableEnergy(energiaActual);
							}
							else energia = false;
						}

					}
					if(this.controladorMovimiento.estadoActual.getActuacion() == 0){
						Coordinate victimCoor = this.coordDestino;
						this.coordDestino = itfusoRecVisSimulador.getEscenario().getCoordenadaLugarSeguro();
						ArrayList<Coordinate> ruta1 = new ArrayList<Coordinate>();
						ruta1.add(coordActuales);
						AlgoritmoRuta alg1=new AlgoritmoRuta(this.coordDestino, this.coordActuales);
						alg1.iniciarCalculoruta(coordActuales, ruta1);
						

						for(int i=0;i<ruta1.size() && !this.finalizar && this.energia ;i++){
							Coordinate punto=ruta1.get(i);
							this.coordActuales.setY(punto.getY());
							this.coordActuales.setX(punto.getX());

							if (itfusoRecVisSimulador != null){
								this.itfusoRecVisSimulador.mostrarPosicionRobot(identRobot, coordActuales);
								this.itfusoRecVisSimulador.mostrarPosicionVictima(this.identDestino,coordActuales);
								victimCoor = coordActuales;
							}										
							this.controladorMovimiento.setCoordenadasActuales(coordActuales);
							Thread.sleep(intervaloEnvioInformesMs);								
							if(energiaActual > 0){
								energiaActual--;
								this.robotStatus.setAvailableEnergy(energiaActual);
							}
							else energia = false;
						}





					}
					if(!energia){
						this.controladorMovimiento.itfProcObjetivos.insertarHecho(new Informacion(VocabularioRosace.MsgRomperRobot));
					}
				}
				else {
					finalizar=true;
					enDestino=false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (enDestino ){
			finalizar = true;
			this.controladorMovimiento.estamosEnDestino(identDestino,this.coordActuales);
			log.debug("Coord Robot En thread  " + identRobot + " en destino -> ("+this.coordActuales.getX() + " , " + this.coordActuales.getY() + ")");
		}
	}


}
