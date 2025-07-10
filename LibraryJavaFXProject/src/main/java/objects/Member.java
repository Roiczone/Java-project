package objects;

public class Member {
    private int id;
    private String name;
    private int age;

    public Member(int id, String name, int age) {this.id = id;this.name = name; this.age = age;}


    public int getId() { return id; }

    public String getName() { return name; }

    public int getAge() { return age; }

    @Override
    public String toString() {return id + ": " + name;}

}

