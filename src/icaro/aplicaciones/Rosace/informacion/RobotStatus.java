/*
 *
 * Clase para gestionar las caracter�sticas del robot:
 * 
 *    -  idRobot -> Identificador del robot
 *    -  availableEnergy -> Energ�a disponible
 *    -  robotCoordinate -> Coordenada inicial. Tambi�n se podr�a utilizar para poner la coordenada actual del robot en un instante dado.
 *    -  healRange -> Rango para poder aplicar con �xito la curaci�n de una victima
 *    -  rangeProximity -> Rango de proximidad para poder ver a otros robots que estan a una distancia dentro del rango especificado.
 *    -  robotCapabilities -> Capacidades. Identifica las habilidades que tiene el robot para curar a una victima. Las capacidades estan relacionadas con los requisitos de la victima. 
 *                            Deben emparejar totalmente con los requisitos de una victima para que el robot pueda curarla totalmente. En caso de no emparejar totalmente
 *                            entonces solo permitir� eliminar los requisitos que emparejan, y la victima todav�a requiere de otros robots para poder compensar los requisitos
 *                            que no emparejaron con las capacidades del robot. 
 *
 */

package icaro.aplicaciones.Rosace.informacion;

import icaro.aplicaciones.agentes.componentesInternos.movimientoCtrl.InfoCompMovimiento;
import java.util.ArrayList;
import java.util.List;

public class RobotStatus {

    private String idRobot;
    private String idRobotRol;
    private int availableEnergy;
    private Coordinate robotCoordinateActual,robotCoordinateAnterior ;
    private float healRange;  
    //Actualmente en nuestra implementacion no se utilizan los atributos rangeProximity y robotCapabilities.
    //No obstante esta clase ya ofrece metodos para poder considerarlos en el futuro 
    private float rangeProximity;
    private final double limiteDespalzamiento = 0.5;
    private List<Integer> robotCapabilities = new ArrayList<Integer>();
    private InfoCompMovimiento infoCompMovt;
    private boolean bloqueado;
        
	//Constructor sin argumentos
	public RobotStatus(){
		robotCoordinateAnterior = new Coordinate(0,0,0);
		
		this.bloqueado = false;
	}
		
	public void setIdRobot(String id){
		this.idRobot = id;
	}
		
	public String getIdRobot(){
		return this.idRobot;
	}
        public void setIdRobotRol(String id){
		this.idRobotRol = id;
	}		
	public String getIdRobotRol(){
		return this.idRobotRol;
	}		
	public synchronized void setAvailableEnergy(int energy){
		this.availableEnergy = energy;
	}	
	public synchronized int getAvailableEnergy(){
		return this.availableEnergy;
	}	
    public synchronized void setRobotCoordinate(Coordinate coord){
        this.robotCoordinateAnterior=robotCoordinateActual;
        this.robotCoordinateActual = coord; 
        if (infoCompMovt != null) infoCompMovt.itfAccesoComponente.setCoordenadasActuales(coord);        
    }
    
    public synchronized Coordinate getRobotCoordinate(){
        if (infoCompMovt != null && infoCompMovt.itfAccesoComponente.getHebraMonitorizacionLlegadaDestino() != null)
        	this.robotCoordinateActual = infoCompMovt.itfAccesoComponente.getCoordenadasActuales();
       return new Coordinate(robotCoordinateActual.getX(),robotCoordinateActual.getY(),robotCoordinateActual.z);
    }
    public void setInfoCompMovt(InfoCompMovimiento compInfo){
        this.infoCompMovt = compInfo;    	
    }
    
    public InfoCompMovimiento getInfoCompMovt(){
    	return this.infoCompMovt;
    }
  
    public void setHealRange(float hr){
    	this.healRange = hr;
    }

    public double getHealRange(){
    	return this.healRange;
    }
 
    public void setRangeProximity(float rp){
    	this.rangeProximity = rp;
    }

    public double getRangeProximity(){
    	return this.rangeProximity;
    }
    
    public void setRobotCapabilities(List<Integer> rc){
           for (int i=0; i<rc.size();i++){        	  
        	   this.robotCapabilities.add(i, rc.get(i));        	   
           }
    }
        
    public List<Integer> getRobotCapabilities(){
    	return this.robotCapabilities;
    }
    public boolean sinMovimientoSignificativo (){
        if (robotCoordinateAnterior == null) return false;
        return (limiteDespalzamiento>=Math.abs(robotCoordinateActual.getY()-robotCoordinateAnterior.getY()) && 
                limiteDespalzamiento>=Math.abs(robotCoordinateActual.getX()-robotCoordinateAnterior.getX()) );
    }
    
    @Override
    public String toString(){    	
    	return "Robot: id->" + this.getIdRobot() + 
                " ; Robot: Rol->" + this.getIdRobotRol() +
    			" ; engergylevel->" + this.getAvailableEnergy() + 
    			" ; coordinate->" + this.getRobotCoordinate() + 
    			" ; healrange->" + this.getHealRange() ;    	    	    	     	
    }
    
    public boolean getBloqueado(){
    	return this.bloqueado;
    }
    
    public void setBloqueado(boolean b){
    	this.bloqueado = b;
    }
}