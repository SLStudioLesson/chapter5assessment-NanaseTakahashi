package com.taskapp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.print.DocFlavor.READER;

import com.taskapp.exception.AppException;
import com.taskapp.logic.TaskLogic;
import com.taskapp.logic.UserLogic;
import com.taskapp.model.User;

public class TaskUI {
    private final BufferedReader reader;

    private final UserLogic userLogic;

    private final TaskLogic taskLogic;

    private User loginUser;

    public TaskUI() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        userLogic = new UserLogic();
        taskLogic = new TaskLogic();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param reader
     * @param userLogic
     * @param taskLogic
     */
    public TaskUI(BufferedReader reader, UserLogic userLogic, TaskLogic taskLogic) {
        this.reader = reader;
        this.userLogic = userLogic;
        this.taskLogic = taskLogic;
    }

    /**
     * メニューを表示し、ユーザーの入力に基づいてアクションを実行します。
     *
     * @see #inputLogin()
     * @see com.taskapp.logic.TaskLogic#showAll(User)
     * @see #selectSubMenu()
     * @see #inputNewInformation()
     */
    public void displayMenu() {
        System.out.println("タスク管理アプリケーションにようこそ!!");

        // 設問1 ログイン画面を表示させるメソッドを追加
        inputLogin();

        // メインメニュー
        boolean flg = true;
        while (flg) {
            try {
                System.out.println("以下1~3のメニューから好きな選択肢を選んでください。");
                System.out.println("1. タスク一覧, 2. タスク新規登録, 3. ログアウト");
                System.out.print("選択肢：");
                String selectMenu = reader.readLine();

                System.out.println();

                switch (selectMenu) {
                    case "1": // 設問2 タスク一覧
                        taskLogic.showAll(loginUser);
                        // 設問4 サブメニューを表示
                        selectSubMenu();
                        break;
                    case "2": // 設問3 タスク新規登録
                        inputNewInformation();
                        break;
                    case "3": // ログアウト
                        System.out.println("ログアウトしました。");
                        flg = false;
                        break;
                    default:
                        System.out.println("選択肢が誤っています。1~3の中から選択してください。");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからのログイン情報を受け取り、ログイン処理を行います。
     *
     * @see com.taskapp.logic.UserLogic#login(String, String)
     */
    public void inputLogin() {
        boolean flg = true;
        while (flg) {
            try {
                System.out.print("メールアドレスを入力してください：");
                String email = reader.readLine();

                System.out.print("パスワードを入力してください：");
                String password = reader.readLine();

                // ログイン処理を呼び出す
                loginUser = userLogic.login(email, password);
                System.out.println();
                flg = false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからの新規タスク情報を受け取り、新規タスクを登録します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#save(int, String, int, User)
     */
    public void inputNewInformation() {
        // 設問3 新規タスク登録に必要な情報の入力を受け付ける
        boolean flg = true;
        while (flg) {
            try {
                System.out.print("タスクコードを入力してください：");
                String code = reader.readLine();
                // バリデーション　タスクコードは数字か
                if (!isNumeric(code)) {
                    System.out.println("コードは半角の数字で入力してください");
                    System.out.println();
                    continue;
                }

                System.out.print("タスク名を入力してください：");
                String name = reader.readLine();
                // バリデーション　タスク名は10文字以内か
                if (!(name.length() <= 10)) {
                    System.out.println("タスク名は10文字以内で入力してください");
                    System.out.println();
                    continue;
                }

                System.out.print("担当するユーザーのコードを選択してください：");
                String repUserCode = reader.readLine();
                // バリデーション　担当するユーザーコードは数字か
                if (!isNumeric(repUserCode)) {
                    System.out.println("ユーザーのコードは半角の数字で入力してください");
                    System.out.println();
                    continue;
                }

                // saveメソッドで新規タスク登録を実行する
                taskLogic.save(Integer.parseInt(code), name, Integer.parseInt(repUserCode), loginUser);
                flg = false;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
            System.out.println();
        }

    }

    /**
     * タスクのステータス変更または削除を選択するサブメニューを表示します。
     *
     * @see #inputChangeInformation()
     * @see #inputDeleteInformation()
     */
    public void selectSubMenu() {
        boolean flg = true;
        while (flg) {
            try {
                System.out.println("以下1~2から好きな選択肢を選んでください。");
                System.out.println("1. タスクのステータス変更, 2. メインメニューに戻る");
                System.out.print("選択肢：");
                String selectMenu = reader.readLine();
                System.out.println();

                switch (selectMenu) {
                    case "1": // タスクのステータス変更
                        inputChangeInformation();
                        break;
                    case "2": // メインメニューに戻る
                        System.out.println("メインメニューに戻ります");
                        flg = false;
                        break;
                    default:
                        System.out.println("選択肢が誤っています。1~2の中から選択してください。");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからのタスクステータス変更情報を受け取り、タスクのステータスを変更します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#changeStatus(int, int, User)
     */
    public void inputChangeInformation() {
        // 設問4 タスクステータス変更に必要な情報の入力を受け付ける
        boolean flg = true;
        while (flg) {
            try {
                System.out.print("ステータスを変更するタスクコードを入力してください：");
                String code = reader.readLine();
                // バリデーション　タスクコードは数字か
                if (!isNumeric(code)) {
                    System.out.println("コードは半角の数字で入力してください");
                    System.out.println();
                    continue;
                }

                System.out.println("どのステータスに変更するか選択してください。");
                System.out.println("1. 着手中, 2. 完了");
                System.out.print("選択肢：");
                String status = reader.readLine();
                // バリデーション　変更後のステータスは数字か
                if (!isNumeric(status)) {
                    System.out.println("ステータスは半角の数字で入力してください");
                    System.out.println();
                    continue;
                // バリデーション　変更後のステータスは、1または2か
                } else if (!(Integer.parseInt(status) == 1 || Integer.parseInt(status) == 2)) {
                    System.out.println("ステータスは1・2の中から選択してください");
                    System.out.println();
                    continue;
                }

                // changeStatusメソッドでタスクステータス更新を実行する
                taskLogic.changeStatus(Integer.parseInt(code), Integer.parseInt(status), loginUser);
                flg = false;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからのタスク削除情報を受け取り、タスクを削除します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#delete(int)
     */
    // public void inputDeleteInformation() {
    // }

    /**
     * 指定された文字列が数値であるかどうかを判定します。
     * 負の数は判定対象外とする。
     *
     * @param inputText 判定する文字列
     * @return 数値であればtrue、そうでなければfalse
     */
    public boolean isNumeric(String inputText) {
        return inputText.chars().allMatch(c -> Character.isDigit((char) c));
    }
}