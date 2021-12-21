package bot.bo;

import java.util.Date;

public class ConsultaGas {


	private int id;
	private int contTotal;
	private int contSemana;
	private int contDia;
	private Date diaTopeSemana;
	private Date diaTope;
	
	public ConsultaGas(int id, int contTotal, int contSemana, int contDia, Date diaTopeSemana, Date diaTope) {
		super();
		this.id = id;
		this.contTotal = contTotal;
		this.contSemana = contSemana;
		this.contDia = contDia;
		this.diaTopeSemana = diaTopeSemana;
		this.diaTope = diaTope;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getContTotal() {
		return contTotal;
	}

	public void setContTotal(int contTotal) {
		this.contTotal = contTotal;
	}

	public int getContSemana() {
		return contSemana;
	}

	public void setContSemana(int contSemana) {
		this.contSemana = contSemana;
	}

	public int getContDia() {
		return contDia;
	}

	public void setContDia(int contDia) {
		this.contDia = contDia;
	}

	public Date getDiaTopeSemana() {
		return diaTopeSemana;
	}

	public void setDiaTopeSemana(Date diaTopeSemana) {
		this.diaTopeSemana = diaTopeSemana;
	}

	public Date getDiaTope() {
		return diaTope;
	}

	public void setDiaTope(Date diaTope) {
		this.diaTope = diaTope;
	}
	
	
		
	
}
