package icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl;

import java.util.concurrent.Semaphore;

import icaro.aplicaciones.Rosace.informacion.Coordinate;
import icaro.aplicaciones.Rosace.informacion.RobotStatus;
import icaro.aplicaciones.Rosace.informacion.Victim;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.imp.HebraMonitorizacionLlegada;

public interface ItfUsoMovimientoCtrl {
	
	public void inicializarInfoMovimiento(Coordinate coordInicial, float velocidadInicial);
	public void moverAdestino(String identDest,Coordinate coordDestino, float velocidadCrucero,int tipoActuacion) ;
	public void moverAdestino(Victim victima,String identDest,Coordinate coordDestino, float velocidadCrucero,int tipoActuacion) ;
	public void cambiaVelocidad( float nuevaVelocidadCrucero) ;
	public void cambiaDestino(String identDest,Coordinate coordDestino) ;
	public Coordinate getCoordenasDestino();
	//       public void actualizarCoordenadas(Coordinate nuevasCoordenadas) ;
	public Coordinate getCoordenadasActuales() ;
	public  void setCoordenadasActuales(Coordinate nuevasCoordenadas) ;
	public void parar();
	public void bloquear();
	public void continuar();
	public boolean estamosEnDestino(String identDestino);
	public void imposibleAvanzarADestino();
	public boolean hebraMonitorizacionCreada();
	public void setRobotStatus(RobotStatus robotStatus);
	public Semaphore getSemaphore();

}