package tiqueto.model;

import tiqueto.IOperacionesWeb;
import tiqueto.EjemploTicketMaster;

public class WebCompraConciertos implements IOperacionesWeb {

	private int entradas_disponibles;
	private boolean ventaCerrada = false;
	private boolean ultimaEntradaComprada = false;

	public WebCompraConciertos() {
		super();
		this.entradas_disponibles = 0;
	}

	@Override
	public synchronized boolean comprarEntrada() {
		// Verifica si hay entradas disponibles
		while (!hayEntradas() && !ventaCerrada) {
			// Si no hay entradas disponibles, espera
			mensajeWeb("No hay entradas disponibles, esperando...");
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		// Si la venta está cerrada o ya se compró la última entrada, no se pueden comprar más entradas
		if (ventaCerrada || (ultimaEntradaComprada && entradas_disponibles == 0)) {
			return false;
		}

		// Reduce la cantidad de entradas disponibles y notifica a los posibles compradores en espera
		this.entradas_disponibles--;
		mensajeWeb("Entrada comprada. Entradas restantes: " + entradas_disponibles);

		// Si es la última entrada, marca el flag para evitar que otros fans la compren
		if (entradas_disponibles == 0) {
			ultimaEntradaComprada = true;
		}

		notifyAll(); // Notifica a los fans que están esperando
		return true;
	}

	@Override
	public synchronized int reponerEntradas(int numeroEntradas) {
		// Repone un número limitado de entradas, teniendo en cuenta las entradas restantes en el evento
		for (int i = 0; i < Math.min(numeroEntradas, EjemploTicketMaster.ENTRADAS_RESTANTES); i++) {
			EjemploTicketMaster.ENTRADAS_RESTANTES--;
			this.entradas_disponibles++;
		}

		// Notifica a los fans que las entradas han sido repuestas
		notifyAll();
		return this.entradas_disponibles;
	}

	@Override
	public synchronized void cerrarVenta() {
		// Marca la venta como cerrada y notifica a todos los fans
		this.ventaCerrada = true;
		notifyAll();
	}

	@Override
	public synchronized boolean hayEntradas() {
		// Devuelve true si hay al menos una entrada disponible, de lo contrario, devuelve false
		return this.entradas_disponibles > 0;
	}

	@Override
	public int entradasRestantes() {
		// Devuelve la cantidad de entradas disponibles en la web de compra
		return this.entradas_disponibles;
	}

	/**
	 * Método a usar para cada impresión por pantalla
	 * @param mensaje Mensaje que se quiere lanzar por pantalla
	 */
	public void mensajeWeb(String mensaje) {
		System.out.println(System.currentTimeMillis() + "| WebCompra: " + mensaje);
	}
}
