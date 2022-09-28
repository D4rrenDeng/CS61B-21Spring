package gitlet;

import edu.princeton.cs.algs4.StdOut;
import jdk.jshell.execution.Util;
import org.apache.commons.math3.stat.inference.GTest;

import java.io.File;
import static gitlet.Utils.*;
import java.util.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Darren Deng
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");
    public static final File STAGE_DIR = join(GITLET_DIR, "stage");
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");
    public static final File stageAddFile = join(STAGE_DIR, "Stage_Add");
    public static final File stageRemoveFile = join(STAGE_DIR, "Stage_Remove");
    public static final File ACTIVEBRANCH_FILE = join(GITLET_DIR, "activeBranch");
    public static final File BRANCH_FILE = join(GITLET_DIR, "branch");
    public static final File HEAD_FILE = join(GITLET_DIR, "head");
    /** stage area for add and remove */
    public static HashMap<String, String> stageToAdd;
    public static HashSet<String> stageToRemove;
    public static String head;
    public static String activeBranch;
    public static HashMap<String, String> branches;
    /* TODO: fill in the rest of this class. */

    public static void initCommand(){
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        STAGE_DIR.mkdir();
        BLOB_DIR.mkdir();
        /** initialize staging area */
        stageToAdd = new HashMap<>();
        stageToRemove = new HashSet<>();
        branches = new HashMap<>();

        writeObject(stageAddFile, stageToAdd);
        writeObject(stageRemoveFile, stageToRemove);
        /** create one commit with initial message */
        Commit newCommit = new Commit("initial commit", new Date(0), null, null);
        /** TODO: need to double check if the commit object name should be in this way */
        /** change file name to SHA-1 rendered as hexadecimal string */
        String sha = sha1(newCommit);
        branches = (HashMap) readObject(BRANCH_FILE, HashMap.class);
        branches.put("master", sha);
        activeBranch = "master";
        head = sha;
        // write everything into files
        writeObject(BRANCH_FILE, branches);
        writeObject(ACTIVEBRANCH_FILE, activeBranch);
        writeObject(HEAD_FILE, head);
        File initialCommitFile = join(COMMIT_DIR, sha);
        writeObject(initialCommitFile, newCommit);
    }

    public static void addCommand(String fileName) {
        File file = join(CWD, fileName);

        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String content = readContentsAsString(file);
        head = (String) readObject(HEAD_FILE, String.class);
        File headCommitFile = join(COMMIT_DIR, head);
        Commit headCommit = (Commit) readObject(headCommitFile, Commit.class);
        stageToAdd = (HashMap) readObject(stageAddFile, HashMap.class);
        stageToRemove = (HashSet) readObject(stageRemoveFile, HashSet.class);

            /** check if the file in the current commit is the same as the working version */
        String sha = sha1(content);
        if (headCommit.containsFile(fileName)) {
            if (sha.equals(headCommit.getFile(fileName))) {
                if (stageToAdd.containsKey(fileName)) {
                    // delete it if the current commit has the same version and the file exists in the stage area
                    stageToAdd.remove(fileName);
                }
                if (stageToRemove.contains(fileName)) {
                    stageToRemove.remove(fileName);
                }
            }
        } else {
            stageToAdd.put(fileName, sha);
        }
        writeObject(stageAddFile, stageToAdd);
        writeObject(stageRemoveFile, stageToRemove);
    }

    public static void commitCommand(String message) {
        head = (String)readObject(HEAD_FILE, String.class);
        File headCommitFile = join(COMMIT_DIR, head);
        Commit headCommit = (Commit)readObject(headCommitFile, Commit.class);
        Commit newHeadCommit = headCommit.copyCommit();
        branches = (HashMap) readObject(BRANCH_FILE, HashMap.class);
        activeBranch = (String)readObject(ACTIVEBRANCH_FILE, String.class);
        stageToAdd = (HashMap) readObject(stageAddFile, HashMap.class);
        stageToRemove = (HashSet) readObject(stageRemoveFile, HashSet.class);
        newHeadCommit.setMessage(message);
        newHeadCommit.setParents(head, null);

        if (stageToAdd.isEmpty() && stageToRemove.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        } else if (message == "") {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        } else {
            // update existing file and add new file based on add staging area, also add every new/updated file to blob dir
            for (Map.Entry<String, String> entry : stageToAdd.entrySet()) {
                newHeadCommit.putFile(entry.getKey(), entry.getValue());
                File newFile = join(CWD, entry.getKey());
                File newBlob = join(BLOB_DIR, entry.getValue());

                String newContent = readContentsAsString(newFile);
                writeContents(newBlob, newContent);
            }
            // remove files from current commit based on rm staging area
            for (String key : stageToRemove) {
                newHeadCommit.removeFile(key);
            }
            String sha = sha1(newHeadCommit);
            head = sha;
            branches.put(activeBranch, sha);
            File newCommitFile = join(COMMIT_DIR, sha);
            writeObject(newCommitFile, newHeadCommit);
            //clear staging area
            stageToAdd.clear();
            stageToRemove.clear();
            writeObject(HEAD_FILE, head);
            writeObject(BRANCH_FILE, branches);
            writeObject(stageAddFile, stageToAdd);
            writeObject(stageRemoveFile, stageToRemove);
        }
    }

    public static void removeCommand(String fileName) {
        File file = join(CWD, fileName);
        head = (String)readObject(HEAD_FILE, String.class);
        File headCommitFile = join(COMMIT_DIR, head);
        Commit headCommit = (Commit) readObject(headCommitFile, Commit.class);
        stageToAdd = (HashMap) readObject(stageAddFile, HashMap.class);
        stageToRemove = (HashSet) readObject(stageRemoveFile, HashSet.class);
        if (!stageToAdd.containsKey(fileName) && !headCommit.containsFile(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if (stageToAdd.containsKey(fileName)) {
            stageToAdd.remove(fileName);
        }
        if (headCommit.containsFile(fileName)) {
            stageToRemove.add(fileName);
            if (file.exists()) {
                file.delete();
            }
        }
        writeObject(stageAddFile, stageToAdd);
        writeObject(stageRemoveFile, stageToRemove);
    }

    public static void logCommand() {
        String curSha = (String)readObject(HEAD_FILE, String.class);
        String log = "";
        while (curSha != null) {
            File curFile = join(COMMIT_DIR, curSha);
            Commit curCommit = (Commit) readObject(curFile, Commit.class);
            if (curCommit.getSecondParent() == null) {
                log = log + "===\ncommit " + curSha + "\nDate: " + curCommit.getDate() + "\n" + curCommit.getMessage() + "\n";
            } else {
                log = log + "===\ncommit " + curSha + "\nMerge: " + curCommit.getParent().substring(0, 7) + " " + curCommit.getSecondParent().substring(0, 7) + "\nDate: " + curCommit.getDate() + "\n" + curCommit.getMessage() + "\n";
            }
            curSha = curCommit.getParent();
        }
        System.out.println(log);
    }

    public static void globalLogCommand() {
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        String log = "";
        for (String commit : commits) {
            File curCommitFile = join(COMMIT_DIR, commit);
            Commit curCommit = (Commit) readObject(curCommitFile, Commit.class);
            if (curCommit.getSecondParent() == null) {
                log = log + "===\ncommit " + commit + "\nDate: " + curCommit.getDate() + "\n" + curCommit.getMessage() + "\n";
            } else {
                log = log + "===\ncommit " + commit + "\nMerge: " + curCommit.getParent().substring(0, 7) + " " + curCommit.getSecondParent().substring(0, 7) + "\nDate: " + curCommit.getDate() + "\n" + curCommit.getMessage() + "\n";
            }
        }
        System.out.println(log);
    }

    public static void findCommand(String message) {
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        String list = "";
        for (String commit : commits) {
            File curCommitFile = join(COMMIT_DIR, commit);
            Commit curCommit = (Commit) readObject(curCommitFile, Commit.class);
            if (curCommit.getMessage().equals(message)) {
                list = list + commit + "\n";
            }
        }
        if (list.isEmpty()) {
            System.out.println("Found no commit with that message.");
        } else {
            System.out.println(list);
        }
    }

    public static void statusCommand() {
        String status = "";
        head = (String) readObject(HEAD_FILE, String.class);
        File headCommitFile = join(COMMIT_DIR, head);
        Commit headCommit = (Commit) readObject(headCommitFile, Commit.class);
        activeBranch = (String) readObject(ACTIVEBRANCH_FILE, String.class);
        branches = (HashMap) readObject(BRANCH_FILE, HashMap.class);
        stageToAdd = (HashMap) readObject(stageAddFile, HashMap.class);
        stageToRemove = (HashSet) readObject(stageRemoveFile, HashSet.class);
        List<String> filesCWD = plainFilenamesIn(CWD);

        // Branch Section
        status = "=== Branches ===\n";
        List<String> sortedBranchList = new ArrayList<>(branches.keySet());
        Collections.sort(sortedBranchList);
        for (String branch : sortedBranchList) {
            if (branch.equals(activeBranch)) {
                branch = "*" + branch;
            }
            status = status + branch + "\n";
        }
        status += "\n";

        // Staged File Section
        status += "=== Staged Files ===\n";
        List<String> sortedStagedList = new ArrayList<>(stageToAdd.keySet());
        Collections.sort(sortedStagedList);
        for (String file : sortedStagedList) {
            status = status + file + "\n";
        }
        status += "\n";

        // Removed File Section
        status += "=== Removed Files ===\n";
        List<String> sortedRemovedList = new ArrayList<>(stageToRemove);
        Collections.sort(sortedRemovedList);
        for (String file : sortedRemovedList) {
            status = status + file + "\n";
        }
        status += "\n";

        // Modifications Not Staged for Commit Section
        status += "=== Modifications Not Staged For Commit\n";
        List<String> modList = new ArrayList<>();
        for (String file : filesCWD) {
            if (headCommit.getFiles().containsKey(file)) {
                File curFile = join(CWD, file);
                String curContent = readContentsAsString(curFile);
                String curSha = sha1(curContent);
                if (!curSha.equals(headCommit.getFiles().get(file)) && !stageToAdd.containsKey(file)) {
                    modList.add(file + " (modified)");
                }
            }
        }
        // check the staging area files in the CWD to see if it's changed or deleted
        for (String file : filesCWD) {
            if (stageToAdd.containsKey(file)) {
                File curFile = join(CWD, file);
                if (curFile.exists()) {
                    String curContent = readContentsAsString(curFile);
                    String curSha = sha1(curContent);
                    if (!curSha.equals(stageToAdd.get(file))) {
                        modList.add(file + " (modified)");
                    }
                } else {
                    modList.add(file + " (deleted)");
                }
            }
        }
        Collections.sort(modList);
        for (String modFile : modList) {
            status = status + modFile + "\n";
        }
        status += "\n";

        // Untracked File Section
        status += "=== Untracked Files ===\n";
        List<String> untrackedList = new ArrayList<>();
        for (String file : filesCWD) {
            File curFile = join(CWD, file);
            if (!stageToRemove.contains(file) && headCommit.getFiles().containsKey(file) && !curFile.exists()) {
                untrackedList.add(file);
            }
        }
        Collections.sort(untrackedList);
        for (String untrackedFile : untrackedList) {
            status = status + untrackedFile + "\n";
        }
        status += "\n";
        System.out.println(status);
    }

    public static void checkoutFileCommand(String fileName) {
        head = (String) readObject(HEAD_FILE, String.class);
        File headCommitFile = join(COMMIT_DIR, head);
        Commit headCommit = (Commit) readObject(headCommitFile, Commit.class);
        String fileSha = headCommit.getFile(fileName);
        File newFile = join(BLOB_DIR, fileSha);
        File newFileCWD = join(CWD, fileName);

        if (!newFile.exists()) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String newContent = readContentsAsString(newFile);
        writeContents(newFileCWD, newContent);
    }

    // check for abbreviated commit ID
    public static String abbCommidIDCheck(String commitID) {
        if (commitID.length() == 6) {
            List<String> commits = plainFilenamesIn(COMMIT_DIR);

            for (String commit : commits) {
                if (commit.length() >= 40) {
                    continue;
                } else {
                    if (commitID.equals(commit.substring(0, 6))) {
                        commitID = commit;
                        break;
                    }
                }
            }
        }
        return commitID;
    }

    public static void checkoutFileCommand(String commitID, String fileName) {
        commitID = abbCommidIDCheck(commitID);
        File commitFile = join(COMMIT_DIR, commitID);

        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit commit = (Commit) readObject(commitFile, Commit.class);
        String fileSha = commit.getFile(fileName);
        File newFile = join(BLOB_DIR, fileSha);
        File newFileCWD = join(CWD, fileName);

        if (!newFile.exists()) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String newContent = readContentsAsString(newFile);
        writeContents(newFileCWD, newContent);
    }

    public static void checkoutBranchCommand(String branch) {
        // delete all the files in the CWD that are tracked in the current branch
        head = (String) readObject(HEAD_FILE, String.class);
        branches = (HashMap) readObject(BRANCH_FILE, HashMap.class);
        activeBranch = (String) readObject(ACTIVEBRANCH_FILE, String.class);

        // if branch does not exist
        if (!branches.containsKey(branch)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        // if branch is the active branch
        if (branch.equals(activeBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        String newHead = branches.get(branch);
        File headCommitFile = join(COMMIT_DIR, head);
        Commit headCommit = (Commit) readObject(headCommitFile, Commit.class);
        HashMap<String, String> headFiles = headCommit.getFiles();
        File newHeadCommitFile = join(COMMIT_DIR, newHead);
        Commit newHeadCommit = (Commit) readObject(newHeadCommitFile, Commit.class);
        HashMap<String, String> newHeadFiles = newHeadCommit.getFiles();
        List<String> filesCWD = plainFilenamesIn(CWD);
        // check if a working file is untracked in the current branch and would be overwritten by the checked-out branch
        for (String file : filesCWD) {
            if (newHeadFiles.containsKey(file) && !headFiles.containsKey(file)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        // delete all the files that are tracked in the current branch but not tracked in the checked-out branch
        for (HashMap.Entry<String, String> entry : headFiles.entrySet()) {
            if (!newHeadFiles.containsKey(entry.getKey())) {
                File file = join(CWD, entry.getValue());
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        // add all the files from the checked-out branch into the CWD
        for (HashMap.Entry<String, String> entry : newHeadFiles.entrySet()) {
            File file = join(BLOB_DIR, entry.getValue());
            String content = readContentsAsString(file);
            File newFileCWD = join(CWD, entry.getKey());
            writeContents(newFileCWD, content);
        }
        // update the current branch
        activeBranch = branch;
        writeObject(ACTIVEBRANCH_FILE, activeBranch);
        // update the new head
        writeObject(HEAD_FILE, newHead);
        //clear staging area
        stageToAdd.clear();
        writeObject(stageAddFile, stageToAdd);
        stageToRemove.clear();
        writeObject(stageRemoveFile, stageToRemove);
    }

    public static void branchCommand(String branchName) {
        branches = (HashMap) readObject(BRANCH_FILE, HashMap.class);
        head = (String) readObject(HEAD_FILE, String.class);

        if (branches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        branches.put(branchName, head);
        writeObject(BRANCH_FILE, branches);
    }

    public static void removeBranchCommand(String branchName) {
        branches = (HashMap) readObject(BRANCH_FILE, HashMap.class);
        head = (String) readObject(HEAD_FILE, String.class);
        activeBranch = (String) readObject(ACTIVEBRANCH_FILE, String.class);

        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (activeBranch.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branches.remove(branchName);
        writeObject(BRANCH_FILE, branches);
    }

    public static void resetCommand(String commitID) {
        commitID = abbCommidIDCheck(commitID);
        activeBranch = (String) readObject(ACTIVEBRANCH_FILE, String.class);
        String branch = activeBranch;
        branches = (HashMap) readObject(BRANCH_FILE, HashMap.class);
        branches.put(activeBranch, commitID);
        writeObject(BRANCH_FILE, branches);
        // temporarily set activeBranch as null to avoid failure case in checkoutCommand
        activeBranch = null;
        writeObject(ACTIVEBRANCH_FILE, activeBranch);
        checkoutBranchCommand(branch);
    }

    public static void mergeCommand(String givenBranch) {
        stageToAdd = (HashMap) readObject(stageAddFile, HashMap.class);
        stageToRemove = (HashSet) readObject(stageRemoveFile, HashSet.class);
        List<String> filesCWD = plainFilenamesIn(CWD);
        // if staging area is not empty
        if (!stageToAdd.isEmpty() || !stageToRemove.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        branches = (HashMap) readObject(BRANCH_FILE, HashMap.class);
        activeBranch = (String) readObject(ACTIVEBRANCH_FILE, String.class);
        head = (String) readObject(HEAD_FILE, String.class);
        // if the branch name does not exist
        if (!branches.containsKey(givenBranch)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        // if merging with itself
        if (activeBranch.equals(givenBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        // find the split point
        HashSet<String> headAncester = new HashSet<>();
        File curCommitFile = join(COMMIT_DIR, head);
        headAncester.add(head);
        Commit curCommit = (Commit) readObject(curCommitFile, Commit.class);
        // find the ancesters of head commit
        while (curCommit.getParent() != null) {
            headAncester.add(curCommit.getParent());
            curCommitFile = join(COMMIT_DIR, curCommit.getParent());
            curCommit = (Commit) readObject(curCommitFile, Commit.class);
        }
        // backtrack the given branche's node and look for split point
        String givenBranchSha = branches.get(givenBranch);
        String curGivenSha = givenBranchSha;
        String splitPointSha = null;
        while (curGivenSha != null) {
            if (headAncester.contains(curGivenSha)) {
                splitPointSha = curGivenSha;
                break;
            }
            File curGivenCommitFile = join(COMMIT_DIR, curGivenSha);
            Commit curGivenCommit = (Commit) readObject(curGivenCommitFile, Commit.class);
            curGivenSha = curGivenCommit.getParent();
        }
        File splitCommitFile = join(COMMIT_DIR, splitPointSha);
        Commit splitCommit = (Commit) readObject(splitCommitFile,Commit.class);
        HashMap<String, String> splitFiles = splitCommit.getFiles();
        File givenCommitFile = join(COMMIT_DIR, givenBranchSha);
        Commit givenCommit = (Commit) readObject(givenCommitFile, Commit.class);
        HashMap<String, String> givenFiles = givenCommit.getFiles();
        HashMap<String, String> curFiles = curCommit.getFiles();
        // If an untracked file in the current commit would be overwitten or deleted by the merge, print error message
        for (String file : filesCWD) {
            if (!curFiles.containsKey(file) && splitFiles.containsKey(file)) {
                System.out.printf("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        // if the split point is the same commit as the given branch
        if (splitPointSha.equals(givenBranchSha)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        // if the split point is the current branch
        if (splitPointSha.equals(head)) {
            checkoutBranchCommand(givenBranch);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        /* Any files that have been modified in the given branch since the split point, but not modified in
        the current branch since the split point should be changed to their versions in the given branch */
        for (String file : filesCWD) {
            if (givenFiles.containsKey(file) && curFiles.containsKey(file)) {
                File fileCWD = join(CWD, file);
                String contentCWD = readContentsAsString(fileCWD);
                String shaCWD = sha1(contentCWD);
                if (shaCWD.equals(curFiles.get(file)) && !shaCWD.equals(givenFiles.get(file))) {
                    File newFile = join(BLOB_DIR, givenFiles.get(file));
                    String newContent = readContentsAsString(newFile);
                    writeContents(fileCWD, newContent);
                    stageToAdd.put(file, givenFiles.get(file));
                }
            }
        }
        /* Any files that were not present at the split point and are present only in the given branch should
        be checked out and staged.*/
        for (HashMap.Entry<String, String> entry : givenFiles.entrySet()) {
            if (!splitFiles.containsKey(entry.getKey()) && !curFiles.containsKey(entry.getKey())) {
                checkoutFileCommand(givenBranchSha, entry.getKey());
                stageToAdd.put(entry.getKey(), entry.getValue());
            }
        }
        for (HashMap.Entry<String, String> entry : splitFiles.entrySet()) {
            /* Any files present at the split point, unmodified in the current branch, and absent in the given branch
            should be removed (and untracked). */
            if (entry.getValue().equals(curFiles.get(entry.getKey())) && !givenFiles.containsKey(entry.getKey())) {
                removeCommand(entry.getKey());
            }
        }
        /* Any files modified in different ways in the current and given branches are in conflict */
        boolean mergeConflict = false;
        for (HashMap.Entry<String, String> entry : splitFiles.entrySet()) {
            // file exists in both branches and the contents of both branches are changed and different from each other
            if (curFiles.containsKey(entry.getKey()) && givenFiles.containsKey(entry.getKey())) {
                if (!entry.getValue().equals(curFiles.get(entry.getKey())) && !entry.getValue().equals(givenFiles.get(entry.getKey())) && !curFiles.get(entry.getKey()).equals(givenFiles.get(entry.getKey()))) {
                    mergeConflict = true;
                    File curFile = join(BLOB_DIR, curFiles.get(entry.getKey()));
                    File givenFile = join(BLOB_DIR, givenFiles.get(entry.getKey()));
                    String curContent = readContentsAsString(curFile);
                    String givenContent = readContentsAsString(givenFile);
                    String newContent = "<<<<<<< HEAD\n" + curContent + "\n=======\n" + givenContent + "\n>>>>>>>\n";
                    String newSha = sha1(newContent);
                    File newFile = join(CWD, entry.getKey());
                    writeContents(newFile, newContent);
                    stageToAdd.put(entry.getKey(), newSha);
                }
            }
            // file exist in only one branch and the content has been changed
            if (curFiles.containsKey(entry.getKey()) && !givenFiles.containsKey(entry.getKey()) && !entry.getValue().equals(curFiles.get(entry.getKey()))) {
                mergeConflict = true;
                File curFile = join(BLOB_DIR, curFiles.get(entry.getKey()));
                String curContent = readContentsAsString(curFile);
                String newContent = "<<<<<<< HEAD\n" + curContent + "\n=======\n\n>>>>>>>\n";
                String newSha = sha1(newContent);
                File newFile = join(CWD, entry.getKey());
                writeContents(newFile, newContent);
                stageToAdd.put(entry.getKey(), newSha);
            }
            if (givenFiles.containsKey(entry.getKey()) && !curFiles.containsKey(entry.getKey()) && !entry.getValue().equals(givenFiles.get(entry.getKey()))) {
                mergeConflict = true;
                File givenFile = join(BLOB_DIR, givenFiles.get(entry.getKey()));
                String givenContent = readContentsAsString(givenFile);
                String newContent = "<<<<<<< HEAD\n\n=======\n" + givenContent + "\n>>>>>>>\n";
                String newSha = sha1(newContent);
                File newFile = join(CWD, entry.getKey());
                writeContents(newFile, newContent);
                stageToAdd.put(entry.getKey(), newSha);
            }
        }
        // if the file is absent at the split point and has different contents in the given and current branches
        for (HashMap.Entry<String, String> entry : curFiles.entrySet()) {
            if (!splitFiles.containsKey(entry.getKey()) && givenFiles.containsKey(entry.getKey()) && !entry.getValue().equals(givenFiles.get(entry.getKey()))) {
                mergeConflict = true;
                File curFile = join(BLOB_DIR, curFiles.get(entry.getKey()));
                File givenFile = join(BLOB_DIR, givenFiles.get(entry.getKey()));
                String curContent = readContentsAsString(curFile);
                String givenContent = readContentsAsString(givenFile);
                String newContent = "<<<<<<< HEAD\n" + curContent + "\n=======\n" + givenContent + "\n>>>>>>>\n";
                String newSha = sha1(newContent);
                File newFile = join(CWD, entry.getKey());
                writeContents(newFile, newContent);
                stageToAdd.put(entry.getKey(), newSha);
            }
        }
        // serialize staging area
        writeObject(stageAddFile, stageToAdd);
        writeObject(stageRemoveFile, stageToRemove);
        String commitMessage = "Merged" + givenBranch + "into" + activeBranch + ".";
        commitCommand(commitMessage);
        if (mergeConflict == true) {
            System.out.println("Encountered a merge conflict.");
        }
        head = (String) readObject(HEAD_FILE, String.class);
        File headCommitFile = join(COMMIT_DIR, head);
        Commit headCommit = (Commit) readObject(headCommitFile, Commit.class);
        headCommit.setParents(head, givenBranchSha);
        writeObject(headCommitFile, headCommit);
    }
}
