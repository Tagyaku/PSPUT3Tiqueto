// Clase FanGrupo

package tiqueto.model;

import tiqueto.EjemploTicketMaster;

public class FanGrupo extends Thread {

	final WebCompraConciertos webCompra;
	int numeroFan;
	private String tabuladores = "\t\t\t\t";
	int entradasCompradas = 0;

	public FanGrupo(WebCompraConciertos web, int numeroFan) {
		super();
		this.numeroFan = numeroFan;
		this.webCompra = web;
	}

	@Override
	public void run() {

		// El hilo del fan se ejecuta mientras no haya alcanzado el límite de entradas por fan
		// y aún haya entradas restantes o la web de compra tiene entradas disponibles.
		while ((entradasCompradas < EjemploTicketMaster.MAX_ENTRADAS_POR_FAN) &&
				(EjemploTicketMaster.ENTRADAS_RESTANTES > 0 || (webCompra.hayEntradas()))) {
			// Sincroniza el acceso a la web de compra para evitar condiciones de carrera
			synchronized (webCompra) {
				// Comprueba si hay entradas disponibles en la web de compra
				if (webCompra.hayEntradas()) {
					mensajeFan("Ahora mismo voy a comprar una entrada!!");
					// Intenta comprar una entrada y actualiza la cantidad de entradas compradas
					if (webCompra.comprarEntrada()) {
						entradasCompradas++;
						mensajeFan("Acabo de comprar una entrada, dispongo de " + entradasCompradas);
					}
				} else {
					// Si no hay entradas disponibles, espera
					mensajeFan("No hay entradas disponibles, esperando...");
					try {
						webCompra.wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
			try {
				// El fan espera un tiempo aleatorio entre 1 y 3 segundos antes de intentar comprar otra entrada
				Thread.sleep((int) ((Math.random() * (3000 - 1000 + 1000)) + 1000));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		// Cuando la venta ha sido cerrada o el fan ha alcanzado su límite de entradas, informa.
		mensajeFan("La venta ha sido cerrada");
	}

	public void dimeEntradasCompradas() {
		mensajeFan("Sólo he conseguido: " + entradasCompradas);
	}

	/**
	 * Método a usar para cada impresión por pantalla
	 * @param mensaje Mensaje que se quiere lanzar por pantalla
	 */
	private void mensajeFan(String mensaje) {
		System.out.println(System.currentTimeMillis() + "|" + tabuladores + " Fan " + this.numeroFan + ": " + mensaje);
	}
}
