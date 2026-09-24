import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Processo {

    private String nome;
    private int instanteChegada;
    private int prioridade;
    private EstadoProcesso estado;

    private int acc;
    private int pc;

    private int fila;
    private int quantumUsado;
    private int tempoBloqueado;

    private int tempoCpu;
    private int tempoEspera;
    private int instanteFinalizacao;

    private List<Instrucao> instrucoes;
    private Map<String, Integer> variaveis;
    private Map<String, Integer> rotulos;

    public Processo(String nome, int instanteChegada, int prioridade) {

        this.nome = nome;
        this.instanteChegada = instanteChegada;
        this.prioridade = prioridade;

        this.estado = EstadoProcesso.PRONTO;

        this.acc = 0;
        this.pc = 0;

        this.fila = 0;
        this.quantumUsado = 0;
        this.tempoBloqueado = 0;

        this.tempoCpu = 0;
        this.tempoEspera = 0;
        this.instanteFinalizacao = -1;

        this.instrucoes = new ArrayList<>();
        this.variaveis = new HashMap<>();
        this.rotulos = new HashMap<>();
    }

    public String getNome() {
        return nome;
    }

    public int getInstanteChegada() {
        return instanteChegada;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public EstadoProcesso getEstado() {
        return estado;
    }

    public void setEstado(EstadoProcesso estado) {
        this.estado = estado;
    }

    public int getAcc() {
        return acc;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public void adicionarInstrucao(Instrucao instrucao) {
        instrucoes.add(instrucao);
    }

    public List<Instrucao> getInstrucoes() {
        return instrucoes;
    }

    public Instrucao getInstrucaoAtual() {

        if (pc < 0 || pc >= instrucoes.size()) {

            throw new IllegalStateException(
                    "PC fora da lista de instrucoes: " + pc
            );
        }

        return instrucoes.get(pc);
    }

    public void definirVariavel(String nome, int valor) {
        variaveis.put(nome, valor);
    }

    public int getVariavel(String nome) {

        if (!variaveis.containsKey(nome)) {

            throw new IllegalArgumentException(
                    "Variavel nao encontrada: " + nome
            );
        }

        return variaveis.get(nome);
    }

    public void definirRotulo(String nome, int indice) {
        rotulos.put(nome, indice);
    }

    public int getRotulo(String nome) {

        if (!rotulos.containsKey(nome)) {

            throw new IllegalArgumentException(
                    "Rotulo nao encontrado: " + nome
            );
        }

        return rotulos.get(nome);
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getQuantumUsado() {
        return quantumUsado;
    }

    public void incrementarQuantum() {
        quantumUsado++;
    }

    public void zerarQuantum() {
        quantumUsado = 0;
    }

    public int getTempoBloqueado() {
        return tempoBloqueado;
    }

    public void setTempoBloqueado(int tempoBloqueado) {
        this.tempoBloqueado = tempoBloqueado;
    }

    public void diminuirTempoBloqueado() {

        if (tempoBloqueado > 0) {
            tempoBloqueado--;
        }
    }

    public int getTempoCpu() {
        return tempoCpu;
    }

    public void incrementarTempoCpu() {
        tempoCpu++;
    }

    public int getTempoEspera() {
        return tempoEspera;
    }

    public void incrementarTempoEspera() {
        tempoEspera++;
    }

    public int getInstanteFinalizacao() {
        return instanteFinalizacao;
    }

    public void setInstanteFinalizacao(int instanteFinalizacao) {
        this.instanteFinalizacao = instanteFinalizacao;
    }
}