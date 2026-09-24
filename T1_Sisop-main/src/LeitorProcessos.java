import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LeitorProcessos {

    public List<Processo> carregarProcessos(
            String caminhoConfiguracao
    ) throws IOException {

        List<String> linhas =
                Files.readAllLines(
                        Path.of(caminhoConfiguracao)
                );

        List<Processo> processos =
                new ArrayList<>();

        LeitorAssembly leitorAssembly =
                new LeitorAssembly();

        for (String linha : linhas) {

            linha = linha.trim();

            if (linha.isEmpty()
                    || linha.startsWith("#")) {
                continue;
            }

            String[] partes =
                    linha.split(";");

            if (partes.length != 4) {

                throw new IllegalArgumentException(
                        "Linha invalida no arquivo de processos: "
                        + linha
                );
            }

            String nome =
                    partes[0].trim();

            int chegada =
                    Integer.parseInt(
                            partes[1].trim()
                    );

            int prioridade =
                    Integer.parseInt(
                            partes[2].trim()
                    );

            String arquivoAssembly =
                    partes[3].trim();

            if (prioridade < 1
                    || prioridade > 5) {

                throw new IllegalArgumentException(
                        "Prioridade deve estar entre 1 e 5: "
                        + prioridade
                );
            }

            Processo processo =
                    new Processo(
                            nome,
                            chegada,
                            prioridade
                    );

            leitorAssembly.carregarPrograma(
                    arquivoAssembly,
                    processo
            );

            processos.add(processo);
        }

        return processos;
    }
}
