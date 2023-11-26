package tiqueto.model;

import tiqueto.EjemploTicketMaster;

public class PromotoraConciertos extends Thread {

	final WebCompraConciertos webCompra;

	public PromotoraConciertos(WebCompraConciertos webCompra) {
		super();
		this.webCompra = webCompra;
	}

	@Override
	public void run() {
		// El hilo de la promotora se ejecuta mientras aún haya entradas restantes
		while (EjemploTicketMaster.ENTRADAS_RESTANTES > 0) {
			// Sincroniza el acceso a la web de compra para evitar condiciones de carrera
			synchronized (webCompra) {
				// Si no hay entradas disponibles en la web de compra, la promotora repone más entradas
				if (!webCompra.hayEntradas()) {
					mensajePromotor("Nos quedamos sin entradas, voy a reponer mas...");
					webCompra.mensajeWeb("AUN QUEDAN UN TOTAL DE " + EjemploTicketMaster.ENTRADAS_RESTANTES);
					webCompra.reponerEntradas(EjemploTicketMaster.REPOSICION_ENTRADAS);
					try {
						// La promotora espera un tiempo aleatorio entre 3 y 8 segundos antes de continuar
						Thread.sleep((int) ((Math.random() * (8000 - 3000 + 3000)) + 3000));
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				} else {
					// Si hay entradas disponibles, espera
					try {
						webCompra.wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		// Notifica a los fans que la promotora ha terminado
		synchronized (webCompra) {
			webCompra.notifyAll();
		}
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		// Cuando no quedan más entradas, la promotora informa y cierra la venta
		mensajePromotor("Ya no quedan entradas");
		webCompra.cerrarVenta();
	}

	/**
	 * Método a usar para cada impresión por pantalla
	 * @param mensaje Mensaje que se quiere lanzar por pantalla
	 */
	public static void mensajePromotor(String mensaje) {
		System.out.println(System.currentTimeMillis() + "| Promotora: " + mensaje);
	}
}
