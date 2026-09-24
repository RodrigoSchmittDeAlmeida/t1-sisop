import java.util.ArrayList;
import java.util.List;

public class Simulador {

    private int tempoAtual;
    private List<Processo> processos;
    private EscalonadorMLFQ escalonador;
    private Interpretador interpretador;
    private Processo processoExecutando;
    private List<Processo> processosAdmitidos;
    private List<String> gantt;

    public Simulador() {
        tempoAtual = 0;
        processos = new ArrayList<>();
        escalonador = new EscalonadorMLFQ();
        interpretador = new Interpretador();
        processoExecutando = null;
        processosAdmitidos = new ArrayList<>();
        gantt = new ArrayList<>();
    }

    public void adicionarProcesso(Processo processo) {
        processos.add(processo);
    }

    public void verificarChegadas() {

        for (Processo processo : processos) {

            if (processo.getInstanteChegada() == tempoAtual
                    && !processosAdmitidos.contains(processo)) {

                escalonador.adicionarFila0(processo);
                processosAdmitidos.add(processo);

                System.out.println(
                        "UT " + tempoAtual
                        + " - " + processo.getNome()
                        + " chegou e entrou na Fila 0."
                );
            }
        }
    }

    public int getTempoAtual() {
        return tempoAtual;
    }

    public void avancarTempo() {
        tempoAtual++;
    }

    public String fila0ComoTexto() {
        return escalonador.fila0ComoTexto();
    }

    public void executarUmaUT() {

        verificarChegadas();

        if (processoExecutando != null
                && escalonador.devePreemptar(processoExecutando)) {

            System.out.println(
                    "UT " + tempoAtual
                    + " - " + processoExecutando.getNome()
                    + " sofreu preempcao."
            );

            escalonador.preemptarFila1(processoExecutando);
            processoExecutando = null;
        }

        if (processoExecutando == null) {
            processoExecutando = escalonador.escolherProximo();
        }

        mostrarEstadoAtual();

        if (processoExecutando == null) {

            System.out.println(
                    "UT " + tempoAtual
                    + " - CPU ociosa."
            );

            gantt.add("-");

            escalonador.atualizarBloqueados();

            avancarTempo();

            return;
        }

        atualizarTempoEspera();

        System.out.println(
                "UT " + tempoAtual
                + " - Executando "
                + processoExecutando.getNome()
                + " - Fila "
                + processoExecutando.getFila()
        );

        gantt.add(processoExecutando.getNome());

        interpretador.executarInstrucao(processoExecutando);

        processoExecutando.incrementarTempoCpu();
        processoExecutando.incrementarQuantum();

        escalonador.atualizarBloqueados();

        if (processoExecutando.getEstado()
                == EstadoProcesso.FINALIZADO) {

            processoExecutando.setInstanteFinalizacao(
                    tempoAtual + 1
            );

            processoExecutando.zerarQuantum();

            System.out.println(
                    processoExecutando.getNome()
                    + " terminou."
            );

            processoExecutando = null;

        } else if (processoExecutando.getEstado()
                == EstadoProcesso.BLOQUEADO) {

            escalonador.adicionarBloqueado(
                    processoExecutando
            );

            processoExecutando.zerarQuantum();

            System.out.println(
                    processoExecutando.getNome()
                    + " foi bloqueado."
            );

            processoExecutando = null;

        } else if (processoExecutando.getQuantumUsado()
                >= escalonador.getQuantum(processoExecutando)) {

            if (processoExecutando.getFila() == 0) {

                System.out.println(
                        processoExecutando.getNome()
                        + " terminou o quantum da Fila 0 "
                        + "e foi para a Fila 1."
                );

                processoExecutando.zerarQuantum();

                escalonador.adicionarFila1(
                        processoExecutando
                );

            } else {

                System.out.println(
                        processoExecutando.getNome()
                        + " terminou o quantum da Fila 1 "
                        + "e voltou para a Fila 1."
                );

                processoExecutando.zerarQuantum();

                escalonador.adicionarFila1(
                        processoExecutando
                );
            }

            processoExecutando = null;
        }

        avancarTempo();
    }

    public boolean todosFinalizados() {

        for (Processo processo : processos) {

            if (processo.getEstado()
                    != EstadoProcesso.FINALIZADO) {

                return false;
            }
        }

        return true;
    }

    public void executar() {

        while (!todosFinalizados()) {
            executarUmaUT();
        }

        System.out.println(
                "\nSimulacao finalizada em "
                + tempoAtual
                + " UTs."
        );

        mostrarGantt();
        mostrarResultados();
    }

    private void atualizarTempoEspera() {

        for (Processo processo : processosAdmitidos) {

            if (processo.getEstado()
                    == EstadoProcesso.PRONTO) {

                processo.incrementarTempoEspera();
            }
        }
    }

    private void mostrarEstadoAtual() {

        System.out.println(
                "\n--- UT "
                + tempoAtual
                + " ---"
        );

        if (processoExecutando == null) {

            System.out.println("CPU: -");

        } else {

            System.out.println(
                    "CPU: "
                    + processoExecutando.getNome()
            );
        }

        System.out.println(
                "Fila 0: "
                + escalonador.fila0ComoTexto()
        );

        System.out.println(
                "Fila 1: "
                + escalonador.fila1ComoTexto()
        );

        System.out.println(
                "Bloqueados: "
                + escalonador.bloqueadosComoTexto()
        );

        System.out.println("Estados:");

        for (Processo processo : processos) {

            if (!processosAdmitidos.contains(processo)) {

                System.out.println(
                        processo.getNome()
                        + ": NaO CHEGOU"
                );

            } else {

                System.out.println(
                        processo.getNome()
                        + ": "
                        + processo.getEstado()
                );
            }
        }
    }

    public void mostrarGantt() {

        System.out.println(
                "\n--- DIAGRAMA DE GANTT ---"
        );

        System.out.printf("%-6s", "UT:");

        for (int i = 0; i < gantt.size(); i++) {
            System.out.printf("%-5d", i);
        }

        System.out.println();

        System.out.printf("%-6s", "CPU:");

        for (String processo : gantt) {
            System.out.printf("%-5s", processo);
        }

        System.out.println();
    }

    public void mostrarResultados() {

        System.out.println(
                "\n--- RESULTADOS DA SIMULAcaO ---"
        );

        int totalEspera = 0;
        int totalTurnaround = 0;

        for (Processo processo : processos) {

            int turnaround =
                    processo.getInstanteFinalizacao()
                    - processo.getInstanteChegada();

            totalEspera += processo.getTempoEspera();
            totalTurnaround += turnaround;

            System.out.println(
                    "\nProcesso: "
                    + processo.getNome()
            );

            System.out.println(
                    "Tempo de CPU: "
                    + processo.getTempoCpu()
                    + " UT"
            );

            System.out.println(
                    "Tempo de espera: "
                    + processo.getTempoEspera()
                    + " UT"
            );

            System.out.println(
                    "Finalizacao: UT "
                    + processo.getInstanteFinalizacao()
            );

            System.out.println(
                    "Turnaround: "
                    + turnaround
                    + " UT"
            );
        }

        if (!processos.isEmpty()) {

            double mediaEspera =
                    (double) totalEspera
                    / processos.size();

            double mediaTurnaround =
                    (double) totalTurnaround
                    / processos.size();

            System.out.printf(
                    "\nTempo medio de espera: %.2f UT%n",
                    mediaEspera
            );

            System.out.printf(
                    "Turnaround medio: %.2f UT%n",
                    mediaTurnaround
            );
        }
    }
}