import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("--- SIMULADOR MLFQ ---");

        Simulador simulador = new Simulador();

        LeitorProcessos leitorProcessos =
                new LeitorProcessos();
        List<Processo> processos =
                leitorProcessos.carregarProcessos(
                        "processos.txt"
                );
        for (Processo processo : processos) {
            simulador.adicionarProcesso(
                    processo
            );
        }
        simulador.executar();
    }
}