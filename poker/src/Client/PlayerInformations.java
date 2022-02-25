package Client;

public class PlayerInformations{


    protected String userName;
    protected int stack;
    protected boolean hasFolded;
    protected int bids;
    protected boolean dealer;

    public PlayerInformations(String userName, int stack) {
        this.userName = userName;
        this.stack = stack;
        this.hasFolded=false;
        this.bids=0;
        this.dealer=false;
    }

    public String getUserName() {
        return userName;
    }
}
