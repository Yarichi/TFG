package icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.imp;

import icaro.aplicaciones.Rosace.informacion.Coordinate;
import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.ItfUsoMovimientoCtrl;

public class RobotBloqueadoPorObstaculo extends EstadoAbstractoMovRobot implements ItfUsoMovimientoCtrl{

    protected HebraMonitorizacionLlegada monitorizacionLlegadaDestino;

	public RobotBloqueadoPorObstaculo(MaquinaEstadoMovimientoCtrl maquinaEstds,
			icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.imp.MaquinaEstadoMovimientoCtrl.EstadoMovimientoRobot identEstadoAcrear) {
		super(maquinaEstds, identEstadoAcrear);
		
	}

	public void bloqueadoPorObstaculo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bloquear() {
		
	}

	@Override
	public void moverAdestino(String identDest, Coordinate coordDestino, float velocidadCrucero) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean estamosEnDestino(String identDest) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Coordinate getCoordenadasActuales() {
		// TODO Auto-generated method stub
		return null;
	}

}