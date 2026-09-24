import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LeitorAssembly {

    public List<String> lerLinhas(String caminho) throws IOException {

        return Files.readAllLines(
                Path.of(caminho)
        );
    }

    public void carregarPrograma(
            String caminho,
            Processo processo
    ) throws IOException {

        List<String> linhas = lerLinhas(caminho);

        boolean dentroCodigo = false;
        boolean dentroDados = false;

        int indiceInstrucao = 0;

        for (String linha : linhas) {

            linha = linha.trim();

            if (linha.isEmpty()) {
                continue;
            }

            if (linha.equalsIgnoreCase(".code")) {

                dentroCodigo = true;
                dentroDados = false;

                continue;
            }

            if (linha.equalsIgnoreCase(".endcode")) {

                dentroCodigo = false;

                continue;
            }

            if (linha.equalsIgnoreCase(".data")) {

                dentroDados = true;
                dentroCodigo = false;

                continue;
            }

            if (linha.equalsIgnoreCase(".enddata")) {

                dentroDados = false;

                continue;
            }

            if (dentroDados) {

                String[] partes =
                        linha.split("\\s+");

                if (partes.length < 2) {

                    throw new IllegalArgumentException(
                            "Declaracao de variavel invalida: "
                            + linha
                    );
                }

                String nome = partes[0];

                int valor =
                        Integer.parseInt(partes[1]);

                processo.definirVariavel(
                        nome,
                        valor
                );

            } else if (dentroCodigo) {

                if (linha.endsWith(":")) {

                    String nomeRotulo =
                            linha.substring(
                                    0,
                                    linha.length() - 1
                            );

                    processo.definirRotulo(
                            nomeRotulo,
                            indiceInstrucao
                    );

                } else {

                    String[] partes =
                            linha.split("\\s+");

                    String comando =
                            partes[0];

                    String operando =
                            partes.length > 1
                                    ? partes[1]
                                    : "";

                    processo.adicionarInstrucao(
                            new Instrucao(
                                    comando,
                                    operando
                            )
                    );

                    indiceInstrucao++;
                }
            }
        }
    }
}