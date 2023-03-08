import static io.Reader.readFile;

public class Main {
    public static void main(String[] args) {
        var filename = "example.ledger";
        var journal = readFile(filename);
        var transactions = journal.getTransactions();
        for (var transaction : transactions) {
            System.out.println(transaction);
        }
    }
}
