package classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pessoa {
    private int codigo;
    private String nome;
    private String rua;
    private String cidade;

    public Pessoa(int codigo, String nome, String rua, String cidade) {
        this.codigo = codigo;
        this.nome = nome;
        this.rua = rua;
        this.cidade = cidade;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public static List<Pessoa> lerPessoas(String arquivo, boolean isEndereco) {
        List<Pessoa> pessoas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            br.readLine();
            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(",");
                if (isEndereco) {
                    if (campos.length == 3) {
                        String rua = campos[0];
                        String cidade = campos[1];
                        int codigo = Integer.parseInt(campos[2]);
                        pessoas.add(new Pessoa(codigo, "", rua, cidade));
                    } else {
                        System.out.println("Linha de endereço inválida: " + linha);
                    }
                } else {
                    if (campos.length == 2) {
                        int codigo = Integer.parseInt(campos[0]);
                        String nome = campos[1];
                        pessoas.add(new Pessoa(codigo, nome, "", ""));
                    } else {
                        System.out.println("Linha de pessoa inválida: " + linha);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pessoas;
    }

    public static void exportarParaCSV(List<Pessoa> pessoas, String arquivo) {
        try (FileWriter writer = new FileWriter(arquivo)) {
            writer.append("codigo,nome,rua,cidade\n");
            for (Pessoa pessoa : pessoas) {
                writer.append(String.valueOf(pessoa.getCodigo()))
                        .append(',')
                        .append(pessoa.getNome())
                        .append(',')
                        .append(pessoa.getRua())
                        .append(',')
                        .append(pessoa.getCidade())
                        .append('\n');
            }
            System.out.println("Dados exportados para " + arquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String arquivoPessoas = "Pessoas.csv";
        String arquivoEnderecos = "Enderecos.csv";
        String arquivoPessoasComEndereco = "PessoasComEndereco.csv";

        List<Pessoa> pessoas = lerPessoas(arquivoPessoas, false);
        List<Pessoa> enderecos = lerPessoas(arquivoEnderecos, true);

        Map<Integer, List<Pessoa>> mapaEnderecos = new HashMap<>();
        for (Pessoa endereco : enderecos) {
            mapaEnderecos.computeIfAbsent(endereco.getCodigo(), k -> new ArrayList<>()).add(endereco);
        }

        List<Pessoa> pessoasComEndereco = new ArrayList<>();
        for (Pessoa pessoa : pessoas) {
            List<Pessoa> enderecosPessoa = mapaEnderecos.get(pessoa.getCodigo());
            if (enderecosPessoa != null) {
                for (Pessoa endereco : enderecosPessoa) {
                    pessoasComEndereco.add(new Pessoa(
                            pessoa.getCodigo(),
                            pessoa.getNome(),
                            endereco.getRua(),
                            endereco.getCidade()));
                }
            }
        }

        exportarParaCSV(pessoasComEndereco, arquivoPessoasComEndereco);
    }
}
