package br.com.nexfe.mb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import br.com.nexfe.constantes.ConstantesStatus;
import br.com.nexfe.dao.DescontoDAO;
import br.com.nexfe.dao.EmpregadoDAO;
import br.com.nexfe.dao.FormaPagamentoDAO;
import br.com.nexfe.dao.LancamentoComercialDAO;
import br.com.nexfe.dao.MatriculaDAO;
import br.com.nexfe.dao.TipoLancamentoDAO;
import br.com.nexfe.entidades.Desconto;
import br.com.nexfe.entidades.Empregado;
import br.com.nexfe.entidades.FormaPagamento;
import br.com.nexfe.entidades.LancamentoComercial;
import br.com.nexfe.entidades.Matricula;
import br.com.nexfe.entidades.TipoLancamento;

@ManagedBean
@ViewScoped
public class LancamentoComercialBean {
	
	private LancamentoComercialDAO lancamentoComercialDAO;
	
	private FormaPagamentoDAO formaPagamentoDAO;
	
	private TipoLancamentoDAO tipoLancamentoDAO;
	
	private MatriculaDAO matriculaDAO;
	
	private EmpregadoDAO empregadoDAO;
	
	private DescontoDAO descontoDAO;
	
	private LancamentoComercial lancamentoComercial;
	
	private LancamentoComercial lancamentoComercialExclusao;
	
	private List<FormaPagamento> formasPagamentos;
	
	private List<TipoLancamento> tiposLancamentos;
	
	private List<Matricula> matriculas;
	
	private List<Empregado> empregados;
	
	private List<Desconto> descontos;
	
	private List<LancamentoComercial> lancamentosComerciais;
	
	private List<LancamentoComercial> lancamentosComerciaisFiltrados;
	
	private int tipo;
		
	private static final int ALUNO = 1;
	
	private static final int EMPREGADO = 2;
	
	public void initAluno() {
		inicializaLancamento();
		setLancamentosComerciais(lancamentoComercialDAO.listarAlunos());
		setMatriculas(matriculaDAO.listar(Matricula.class));
		setTipo(ALUNO);
	}
	
	public void initEmpregado() {
		inicializaLancamento();
		setLancamentosComerciais(lancamentoComercialDAO.listarEmpregados());
		setEmpregados(empregadoDAO.listarProfessores());
		setTipo(EMPREGADO);
	}
	
	private void inicializaLancamento() {
		lancamentoComercialDAO = new LancamentoComercialDAO();
		formaPagamentoDAO = new FormaPagamentoDAO();
		tipoLancamentoDAO = new TipoLancamentoDAO();
		matriculaDAO = new MatriculaDAO();
		empregadoDAO = new EmpregadoDAO();
		descontoDAO = new DescontoDAO();
		setFormasPagamentos(formaPagamentoDAO.listarDataAtual());
		setTiposLancamentos(tipoLancamentoDAO.listar(TipoLancamento.class));
		setDescontos(descontoDAO.listarDataAtual());
		setLancamentoComercial(null);
	}

	public void back() {
		setLancamentoComercial(null);
	}

	public void add() {
		setLancamentoComercial(new LancamentoComercial());
	}
	
	public void edit(LancamentoComercial l){
		setLancamentoComercial(l);
	}
	
	public void saveAndUpdate() {
		alteraStatus();
		if (getLancamentoComercial().getIdLancamentoComercial() != null) {
			if (getLancamentoComercial().getIdLancamentoComercial() > 0) {
				lancamentoComercialDAO.alterar(getLancamentoComercial());	
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Alterado com sucesso!"));
			}
		} else {
			lancamentoComercialDAO.salvar(getLancamentoComercial());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Incluido com sucesso!"));
		}
		if(getTipo() == ALUNO) {
			initAluno();
		} else {
			initEmpregado();
		}
	}
	
	private void alteraStatus() {
		if(getLancamentoComercial().getDtPrevista() == null && getLancamentoComercial().getDtRecebimento() == null) {
			getLancamentoComercial().setStatus(ConstantesStatus.SEM_PREVISAO.getNome());
		} else if(getLancamentoComercial().getDtPrevista() != null && getLancamentoComercial().getDtRecebimento() == null) {
			getLancamentoComercial().setStatus(ConstantesStatus.AGUARDANDO_PAGAMENTO.getNome());
		} else if(getLancamentoComercial().getDtRecebimento() != null) {
			if(getLancamentoComercial().getDtPrevista() == null || 
					getLancamentoComercial().getDtRecebimento().compareTo(getLancamentoComercial().getDtPrevista()) <= 0) {
				getLancamentoComercial().setStatus(ConstantesStatus.PAGO_EM_DIA.getNome());
			} else {
				getLancamentoComercial().setStatus(ConstantesStatus.PAGO_COM_ATRASO.getNome());
			}
		}
	}
	
	public void selectDelete(LancamentoComercial l){
		setLancamentoComercialExclusao(l);
	}
	
	//public boolean canDelete(LancamentoComercial l) {
	//	return l.get() == null || l.get().isEmpty();
	//}
	   
	public void delete(){
		lancamentoComercialDAO.excluir(getLancamentoComercialExclusao());
		setLancamentoComercialExclusao(null);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Excluido com sucesso!"));
		if(getTipo() == ALUNO) {
			initAluno();
		} else {
			initEmpregado();
		}
	}
	
	public List<FormaPagamento> autoCompleteFormaPagamento(String query) {
		List<FormaPagamento> sugestoes = new ArrayList<FormaPagamento>();
		for (FormaPagamento f : getFormasPagamentos()) {
			if (f.getNome().toUpperCase().startsWith(query.toUpperCase())) {
				sugestoes.add(f);
			}
		}
		return sugestoes;
	}
	
	public List<TipoLancamento> autoCompleteTipoLancamento(String query) {
		List<TipoLancamento> sugestoes = new ArrayList<TipoLancamento>();
		for (TipoLancamento t : getTiposLancamentos()) {
			if (t.getDescricao().toUpperCase().startsWith(query.toUpperCase())) {
				sugestoes.add(t);
			}
		}
		return sugestoes;
	}
	
	public List<Matricula> autoCompleteMatricula(String query) {
		List<Matricula> sugestoes = new ArrayList<Matricula>();
		for (Matricula m : getMatriculas()) {
			if (m.getAluno().getNome().toUpperCase().startsWith(query.toUpperCase())) {
				sugestoes.add(m);
			}
		}
		return sugestoes;
	}
	
	public List<Desconto> autoCompleteDesconto(String query) {
		List<Desconto> sugestoes = new ArrayList<Desconto>();
		for (Desconto d : getDescontos()) {
			if (d.getNome().toUpperCase().startsWith(query.toUpperCase())) {
				sugestoes.add(d);
			}
		}
		return sugestoes;
	}
	
	public List<Empregado> autoCompleteEmpregado(String query) {
		List<Empregado> sugestoes = new ArrayList<Empregado>();
		for (Empregado e : getEmpregados()) {
			if (e.getNome().toUpperCase().startsWith(query.toUpperCase())) {
				sugestoes.add(e);
			}
		}
		return sugestoes;
	}
	
	////////////////////GETTERS AND SETTERS\\\\\\\\\\\\\\\\\\\\\\\\\\

	public LancamentoComercial getLancamentoComercial() {
		return lancamentoComercial;
	}

	public void setLancamentoComercial(LancamentoComercial lancamentoComercial) {
		this.lancamentoComercial = lancamentoComercial;
	}

	public LancamentoComercial getLancamentoComercialExclusao() {
		return lancamentoComercialExclusao;
	}

	public void setLancamentoComercialExclusao(LancamentoComercial lancamentoComercialExclusao) {
		this.lancamentoComercialExclusao = lancamentoComercialExclusao;
	}

	public List<FormaPagamento> getFormasPagamentos() {
		return formasPagamentos;
	}

	public void setFormasPagamentos(List<FormaPagamento> formasPagamentos) {
		this.formasPagamentos = formasPagamentos;
	}

	public List<LancamentoComercial> getLancamentosComerciais() {
		return lancamentosComerciais;
	}

	public void setLancamentosComerciais(List<LancamentoComercial> lancamentosComerciais) {
		this.lancamentosComerciais = lancamentosComerciais;
	}

	public List<LancamentoComercial> getLancamentosComerciaisFiltrados() {
		return lancamentosComerciaisFiltrados;
	}

	public void setLancamentosComerciaisFiltrados(List<LancamentoComercial> lancamentosComerciaisFiltrados) {
		this.lancamentosComerciaisFiltrados = lancamentosComerciaisFiltrados;
	}

	public List<TipoLancamento> getTiposLancamentos() {
		return tiposLancamentos;
	}

	public void setTiposLancamentos(List<TipoLancamento> tiposLancamentos) {
		this.tiposLancamentos = tiposLancamentos;
	}

	public List<Matricula> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(List<Matricula> matriculas) {
		this.matriculas = matriculas;
	}
	
	public List<Desconto> getDescontos() {
		return descontos;
	}

	public void setDescontos(List<Desconto> descontos) {
		this.descontos = descontos;
	}

	public List<Empregado> getEmpregados() {
		return empregados;
	}

	public void setEmpregados(List<Empregado> empregados) {
		this.empregados = empregados;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
}
