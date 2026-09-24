import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class EscalonadorMLFQ {

    private Deque<Processo> fila0;
    private List<Deque<Processo>> fila1;
    private List<Processo> bloqueados;
    private static final int QUANTUM_FILA0 = 2;
    private static final int QUANTUM_FILA1 = 4;

    public EscalonadorMLFQ() {
        fila0 = new ArrayDeque<>();
        fila1 = new ArrayList<>();
        bloqueados = new ArrayList<>();
        for (int i = 0; i <= 5; i++) {
            fila1.add(new ArrayDeque<>());
        }
    }

    public void adicionarFila0(Processo processo) {
        processo.setFila(0);
        processo.setEstado(EstadoProcesso.PRONTO);
        fila0.addLast(processo);
    }

    public void adicionarFila1(Processo processo) {
        processo.setFila(1);
        processo.setEstado(EstadoProcesso.PRONTO);
        fila1.get(processo.getPrioridade()).addLast(processo);
    }

    public void adicionarBloqueado(Processo processo) {
        processo.setEstado(EstadoProcesso.BLOQUEADO);
        bloqueados.add(processo);
    }

    public void atualizarBloqueados() {
        int i = 0;
        while (i < bloqueados.size()) {
            Processo processo = bloqueados.get(i);
            processo.diminuirTempoBloqueado();
            if (processo.getTempoBloqueado() == 0) {
                bloqueados.remove(i);
                adicionarFila0(processo);
            } else {
                i++;
            }
        }
    }

    public Processo escolherProximo() {
        if (!fila0.isEmpty()) {
            Processo processo = fila0.removeFirst();
            processo.setEstado(EstadoProcesso.EXECUTANDO);
            return processo;
        }

        for (int prioridade = 5; prioridade >= 0; prioridade--) {
            if (!fila1.get(prioridade).isEmpty()) {
                Processo processo =
                        fila1.get(prioridade).removeFirst();
                processo.setEstado(
                        EstadoProcesso.EXECUTANDO
                );
                return processo;
            }
        }
        return null;
    }

    public boolean devePreemptar(Processo processoExecutando) {
        return processoExecutando != null
                && processoExecutando.getFila() == 1
                && !fila0.isEmpty();
    }

    public void preemptarFila1(Processo processo) {
        if (processo != null
            && processo.getFila() == 1) {
        processo.setFila(1);
        processo.setEstado(EstadoProcesso.PRONTO);
        fila1.get(
                processo.getPrioridade()
        ).addFirst(processo);
        }
    }
    public int getQuantum(Processo processo) {
        if (processo.getFila() == 0) {
            return QUANTUM_FILA0;
        }
        return QUANTUM_FILA1;
    }
    public String fila0ComoTexto() {
        String resultado = "";
        for (Processo processo : fila0) {
            resultado += processo.getNome() + " ";
        }
        return resultado.isEmpty()
                ? "-"
                : resultado;
    }
    public String fila1ComoTexto() {
        String resultado = "";
        for (int prioridade = 5;
             prioridade >= 0;
             prioridade--) {
            for (Processo processo : fila1.get(prioridade)) {
                resultado += processo.getNome()
                        + "(P"
                        + prioridade
                        + ") ";
            }
        }
        return resultado.isEmpty()
                ? "-"
                : resultado;
    }
    public String bloqueadosComoTexto() {
        String resultado = "";
        for (Processo processo : bloqueados) {
            resultado += processo.getNome()
                    + "("
                    + processo.getTempoBloqueado()
                    + " UT) ";
        }
        return resultado.isEmpty()
                ? "-"
                : resultado;
    }
}