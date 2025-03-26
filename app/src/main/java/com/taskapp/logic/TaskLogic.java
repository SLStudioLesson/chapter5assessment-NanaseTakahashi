package com.taskapp.logic;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;

import java.time.LocalDate;
// 以下のimport文を追加
import java.util.List;
import com.taskapp.model.User;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.exception.AppException;


public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        // findAllメソッドを実行して、データの一覧を取得
        List<Task> tasks = taskDataAccess.findAll();

        tasks.forEach(task -> {
            // statusの値に応じて表示を変える
            String status = "未着手";
            if (task.getStatus() == 1) {
                status = "着手中";
            } else if (task.getStatus() == 2) {
                status = "完了";
            }
            // タスクを担当するユーザーの名前を表示する
            String name = task.getRepUser().getName();
                // 担当者が今ログインしてるユーザーの場合、
                if (task.getRepUser().getCode() == loginUser.getCode()) {
                    // あなたが担当しています と表示する。
                    name = "あなた";
                }

            // 取得したデータを表示する
            System.out.println(task.getCode() + ". タスク名：" + task.getName() + ", 担当者名：" + name + "が担当しています" + ", ステータス：" + status);
        });
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
                    User loginUser) throws AppException {
    // 設問3
        // repUserCodeから、タスクの担当ユーザーのデータ repUserを取得する
        User repUser = userDataAccess.findByCode(repUserCode);

        // 例外　担当するユーザーコードが users.csvに登録されていない場合、AppExceptionをスローする
        if (repUser == null) throw new AppException("存在するユーザーコードを入力してください");

        // 追加するTaskオブジェクトを生成する
            // Statusは0
            Task task = new Task(code, name, 0, repUser);
        // task.csvにデータを1件追加する
        taskDataAccess.save(task);

        // Logオブジェクトを生成する
            // Statusは 0
            // Change_User_Codeは今ログインしてるユーザーコード
            // Change_Dateは今日の日付
            Log log = new Log(code, loginUser.getCode(), 0, LocalDate.now());
        // logs.csvにデータを1件作成する
        logDataAccess.save(log);
        
        // 完了後にメッセージを出力する
        System.out.println(name + "の登録が完了しました。");
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status,
                            User loginUser) throws AppException {
    // 設問4
        // codeから、変更対象のタスクのデータ taskを取得する
        Task task = taskDataAccess.findByCode(code); // updateTask

        // 例外　入力されたタスクコードが tasks.csvに存在しない場合、AppExceptionをスローする
        if (task == null) throw new AppException("存在するタスクコードを入力してください");

        // 例外　tasks.csvに存在するタスクのステータスが、変更後のステータスの1つ前じゃない場合、例外をスローする // 未着手0 着手中1 完了2
        AppException appException = new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
        if (task.getStatus() == 0 && status == 2) {
            throw appException;
        } else if (task.getStatus() == 1 && status == 1) {
            throw appException;
        } else if (task.getStatus() == 2 && status != 2) {
            throw appException;
        }

        // tasks.csvの該当タスクのステータスを変更後のステータスに更新する
        taskDataAccess.update(task);

        // Logオブジェクトを生成する
            // Statusは変更後のステータス
            // Change_User_Codeは今ログインしてるユーザーコード
            // Change_Dateは今日の日付
            Log log = new Log(code, loginUser.getCode(), task.getStatus(), LocalDate.now());
        // logs.csvにデータを1件作成する
        logDataAccess.save(log);
        
        // 完了後にメッセージを出力する
        System.out.println("ステータスの変更が完了しました。");

    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    // public void delete(int code) throws AppException {
    // }
}