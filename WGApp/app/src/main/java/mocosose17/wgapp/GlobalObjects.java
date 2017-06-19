package mocosose17.wgapp;



public class GlobalObjects{
    private static GlobalObjects instance;
    private String username;

    protected GlobalObjects(){}

    public static GlobalObjects getInstance(){
        if(instance == null){
            synchronized (GlobalObjects.class) {
                if(instance == null) {
                    instance = new GlobalObjects();
                }
            }
        }
        return instance;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }
}