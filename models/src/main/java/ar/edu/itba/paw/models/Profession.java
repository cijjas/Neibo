package ar.edu.itba.paw.models;

public class Profession {
    private final long professionId;
    private final String profession;

    private Profession(Builder builder) {
        this.professionId = builder.professionId;
        this.profession = builder.profession;
    }

    public long getProfessionId() {
        return professionId;
    }

    public String getProfession() {
        return profession;
    }

    @Override
    public String toString() {
        return "Profession{" +
                "professionId=" + professionId +
                ", profession='" + profession + '\'' +
                '}';
    }

    public static class Builder {
        private long professionId;
        private String profession;

        public Builder professionId(long professionId) {
            this.professionId = professionId;
            return this;
        }

        public Builder profession(String profession) {
            this.profession = profession;
            return this;
        }

        public Profession build() {
            return new Profession(this);
        }
    }
}
