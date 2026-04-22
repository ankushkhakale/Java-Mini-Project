public final class InputValidator {

    private InputValidator() {
    }

    public static String normalizeName(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().replaceAll("\\s+", " ");
    }

    public static String validateCandidateName(String rawName) {
        String name = normalizeName(rawName);
        if (name.isEmpty()) {
            return "Candidate name is required.";
        }
        if (!name.matches("[A-Za-z][A-Za-z .'-]{1,29}")) {
            return "Candidate name must be 2-30 characters and contain only letters, space, . ' or -.";
        }
        return "";
    }

    public static String validateVoterName(String rawName) {
        String name = normalizeName(rawName);
        if (name.isEmpty()) {
            return "Voter name is required.";
        }
        if (!name.matches("[A-Za-z][A-Za-z .'-]{1,39}")) {
            return "Voter name must be 2-40 characters and contain only letters, space, . ' or -.";
        }
        return "";
    }
}
