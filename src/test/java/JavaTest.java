/* Copyright (c) 2020 David Tanzer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import net.davidtanzer.krecord.Record;

public class JavaTest {
    public static void main(String[] args) {
        RecoveryData recoveryData = Record.from(RecoveryData.class, new RecoveryData() {
            @Override public String getPhoneNumber() { return "+43-123-45 67 890"; }
            @Override public String getPin() { return "1234"; }
        });
        User user = Record.from(User.class, new User() {
            @Override public String getUserName() { return "jenny"; }
            @Override public String getEmailAddress() { return "jenny@example.com"; }
            @Override public String getPassword() { return "53cur3"; }
            @Override public RecoveryData getRecoveryData() { return recoveryData; }
        });

        User user2 = user.with((current, setter) -> setter
                .set(current.getPassword(), "3v3nm0r353cur3")
                .set(current.getRecoveryData().getPin(), "123456"));

        System.out.println(user);
        System.out.println(user2);
    }
}
