package com.taskapp.dataaccess;

// 設問2 以下のimport文を追加
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskDataAccess {

    private final String filePath;

    private final UserDataAccess userDataAccess;

    public TaskDataAccess() {
        filePath = "app/src/main/resources/tasks.csv";
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     * @param userDataAccess
     */
    public TaskDataAccess(String filePath, UserDataAccess userDataAccess) {
        this.filePath = filePath;
        this.userDataAccess = userDataAccess;
    }

    /**
     * CSVから全てのタスクデータを取得します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @return タスクのリスト
     */
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<Task>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // 先頭行をスキップ

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                // CSVに間違いがあったらスキップする
                if (values.length != 4) continue;

                // 値を各変数に保存する
                int code = Integer.parseInt(values[0]);
                String name = values[1];
                int status = Integer.parseInt(values[2]);
                // UserDataAccessのfindByCodeメソッドから、repUserの値を取得する
                User repUser = userDataAccess.findByCode(Integer.parseInt(values[3]));

                // Taskオブジェクトにマッピングする
                Task task = new Task(code, name, status, repUser);
                // tasksに追加する
                tasks.add(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    /**
     * タスクをCSVに保存します。
     * @param task 保存するタスク
     */
    public void save(Task task) {
        // 設問3
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            String line = createLine(task);
            writer.newLine(); // 改行を追加する
            writer.write(line); // データを1行分追加する
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを1件取得します。
     * @param code 取得するタスクのコード
     * @return 取得したタスク
     */
    public Task findByCode(int code) {
        Task task = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                // タスクコードが一致しなかったらスキップする
                int taskCode = Integer.parseInt(values[0]);
                if (taskCode != code) continue;

                // 一致した場合は他の値も変数に保存する
                String name = values[1];
                int status = Integer.parseInt(values[2]);
                int repUserCode = Integer.parseInt(values[3]);
                // repuserCodeからユーザーのデータを取得する
                User repUser = userDataAccess.findByCode(repUserCode);

                // Userオブジェクトにマッピングする
                task = new Task(repUserCode, name, status, repUser);
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return task;
    }

    /**
     * タスクデータを更新します。
     * @param updateTask 更新するタスク
     */
    public void update(Task updateTask) {
    // 設問4
        List<Task> tasks = findAll(); // タスク一覧リストを取得する
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // CSVのヘッダーを書き込む
            writer.write("Code,Name,Status,Rep_User_Code\n");
            String line;
            for (Task task : tasks) {
                // 編集対象のtaskなら、updateTaskの情報を書き込む
                if (task.getCode() == updateTask.getCode()) {
                    line = createLine(updateTask);
                } else {
                    line = createLine(task);
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを削除します。
     * @param code 削除するタスクのコード
     */
    // public void delete(int code) {
    //     try () {

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    /**
     * タスクデータをCSVに書き込むためのフォーマットを作成します。
     * @param task フォーマットを作成するタスク
     * @return CSVに書き込むためのフォーマット文字列
     */
    private String createLine(Task task) {
        // 設問3
        return task.getCode() + "," + task.getName() + "," + task.getStatus() + "," + task.getRepUser().getCode();
    }
}