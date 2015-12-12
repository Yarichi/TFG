Instrucciones ejecuci�n del proyecto en Eclipse:
	Este proyecto incorpora los archivos que eclipse genera cuando creas un proyecto(classpath y .project), por lo que no es necesario crear un proyecto para incorporar este proyecto.
	Lo que hay que hacer es lo siguiente:
		-File -> Import...
		-Seleccionamos la import source "Existing Projects into Workspace" y damos a Next
		-Ahora, en la ventana Import Projects, seleccionamos la pesta�a Select root directory, y damos a "browse".
		-Aqu� tenemos que seleccionar la carpeta que contiene el proyecto(la carpeta que contiene las dem�s carpetas src,config,lib,etc...)
		-Finalmente damos a finalizar. 
		Con esto ya tendriamos importado el proyecto correctamente al eclipse, pero aun faltar�a algo m�s: las librerias. 
		Github no sincroniza al repositorio los archivos de librerias, por lo que los tienes que a�adir tu a la carpeta lib manualmente. 
		Se facilitan las librerias que se usan en el siguiente enlace: 
			https://drive.google.com/file/d/0B1svaDEcqxkaU00wSzJGSHAzX0E/view?usp=sharing
		
		Es un zip, descomprimimos y contiene una carpeta lib que son todas las librerias, y las copiamos al directorio lib del proyecto.
		
		
		Si se han seguido correctamente estos pasos tendr�is el proyecto instalado correctamente en eclipse, y ya solo quedaria la ejecuci�n.
		
		
		
		Ejecuci�n:
			Para realizar la ejecuci�n correctamente en eclipse hay que crear un perfil de ejecuci�n del proyecto
			lanzando el main de la clase "ArranqueSistemaConCrtlGestorO" localizada en "icaro.infraestructura.clasesGeneradorasOrganizacion." y 
			pas�ndole como argumentos el nombre del xml de la organizacion a ejecutar.
			
			Paso a paso:
				-Una vez instalado el proyecto correctamente en eclipse,clicamos en la flecha del boton de "run" verde que hay en la parte de arriba y seleccionamos "Run configurations...". 
				-Nos saldr� una ventana de configuraciones, y en la parte izquierda nos aparece una serie de tipos de ejecuci�n; ah� tenemos que clicar derecho en "java application" y darle a "new".
				-Ahora, tenemos que configurar la configuraci�n:
																- En la pesta�a Main tenemos que seleccionar en "Project" nuestro proyecto y despues en "Main Class" buscamos la clase mencionada anteriormente
																	o directamente ponemos "icaro.infraestructura.clasesGeneradorasOrganizacion.ArranqueSistemaConCrtlGestorO"
																- En la pesta�a Arguments tenemos que ir al cuadro de texto "Program Arguments" y escribir ah� el nombre del xml de configuraci�n que queramos ejecutar.
																  Actualmente, usamos dos archivos de configuracion(lo que hay que poner en "Program Arguments" es cualquiera de las dos descripciones de a continuacion)
																		-"descripcion_4Robots_6VictimasIP_001_Frecuencia10seg_JerarquicoCR"        --> Esta descripci�n es con 2 obstaculos, 4 robots y 6 victimas.
																		-"descripcion_4Robots_6VictimasIP_001_Frecuencia10seg_JerarquicoCR2"       --> Esta descripcion es sin obstaculos, 4 robots y 6 victimas
																  Por �ltimo, hay que aumentar el tama�o del stack(la pila de llamadas) porque se agota con el tama�o por defecto. Para aumentar el tama�o de la pila
																  hay que a�adir en "VM Arguments" lo siguiente: "-Xss16m".
																  
		Si todo ha ido bien las configuraciones y el proyecto os debe quedar como en las fotos de a continuacion:
		
		Proyecto en eclipse --> https://pixabay.com/photo-1089640/
		Configuracion(Main) --> https://pixabay.com/photo-1089641/
		Configuracion(Arguments) --> https://pixabay.com/photo-1089642/