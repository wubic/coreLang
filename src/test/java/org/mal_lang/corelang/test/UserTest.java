package org.mal_lang.corelang.test;

import core.Attacker;
import org.junit.jupiter.api.Test;

public class UserTest extends CoreLangTest {
    private static class UserTestModel {
    
        public final User user = new User("user");
        
        public final Credentials credentials = new Credentials("credentials");
        public final Application application = new Application("application");
        public Identity identity;

        public UserTestModel(boolean twoFA) {
          identity = new Identity("identity", twoFA);
          user.addUserIds(identity);
          identity.addCredentials(credentials);
          identity.addExecPrivApps(application);
        }

        public void addAttacker(Attacker attacker) {
          attacker.addAttackPoint(user.attemptSocialEngineering);
        }

    }

    private static class UserTestModelNoCreds {
    
        public final User user = new User("user");
        
        public final Application application = new Application("application");
        public Identity identity;

        public UserTestModelNoCreds(boolean twoFA) {
          identity = new Identity("identity", twoFA);
          user.addUserIds(identity);
          identity.addExecPrivApps(application);
        }

        public void addAttacker(Attacker attacker) {
          attacker.addAttackPoint(user.attemptSocialEngineering);
        }
    }

    @Test
    public void testPhishingWith2FA() {
        printTestName(Thread.currentThread().getStackTrace()[1].getMethodName());
        var model = new UserTestModel(true);

        var attacker = new Attacker();
        model.addAttacker(attacker);
        attacker.attack();

        model.user.phishUser.assertCompromisedInstantaneously();
        model.user.phishUser.assertCompromisedInstantaneously();
        model.user.attemptCredentialTheft.assertCompromisedWithEffort();
        model.user.attemptReverseTakeover.assertCompromisedWithEffort();
        model.user.attemptSteal2FAtoken.assertCompromisedWithEffort();
        model.user.steal2FAtoken.assertCompromisedWithEffort();

        model.credentials.credentialTheft.assertCompromisedWithEffort();
        model.application.networkConnect.assertCompromisedWithEffort();

        model.identity.assume.assertCompromisedWithEffort();
    }

    @Test
    public void testPhishingWithout2FA() {
        printTestName(Thread.currentThread().getStackTrace()[1].getMethodName());
        var model = new UserTestModel(false);

        var attacker = new Attacker();
        model.addAttacker(attacker);
        attacker.attack();

        model.user.phishUser.assertCompromisedInstantaneously();
        model.user.phishUser.assertCompromisedInstantaneously();
        model.user.attemptCredentialTheft.assertCompromisedWithEffort();
        model.user.attemptReverseTakeover.assertCompromisedWithEffort();
        model.user.attemptSteal2FAtoken.assertCompromisedWithEffort();
        model.user.steal2FAtoken.assertCompromisedWithEffort();

        model.credentials.credentialTheft.assertCompromisedWithEffort();
        model.application.networkConnect.assertCompromisedWithEffort();

        model.identity.assume.assertCompromisedWithEffort();
    }

    @Test
    public void testPhishingWith2FAnoCreds() {
        printTestName(Thread.currentThread().getStackTrace()[1].getMethodName());
        var model = new UserTestModelNoCreds(true);

        var attacker = new Attacker();
        model.addAttacker(attacker);
        attacker.attack();

        model.user.phishUser.assertCompromisedInstantaneously();
        model.user.phishUser.assertCompromisedInstantaneously();
        model.user.attemptCredentialTheft.assertCompromisedWithEffort();
        model.user.attemptReverseTakeover.assertCompromisedWithEffort();
        model.user.attemptSteal2FAtoken.assertUncompromised();
        model.user.steal2FAtoken.assertUncompromised();

        model.identity.assume.assertUncompromised();
    }

    @Test
    public void testPhishingWithout2FAnoCreds() {
        printTestName(Thread.currentThread().getStackTrace()[1].getMethodName());
        var model = new UserTestModelNoCreds(false);

        var attacker = new Attacker();
        model.addAttacker(attacker);
        attacker.attack();

        model.user.phishUser.assertCompromisedInstantaneously();
        model.user.phishUser.assertCompromisedInstantaneously();
        model.user.attemptCredentialTheft.assertCompromisedWithEffort();
        model.user.attemptReverseTakeover.assertCompromisedWithEffort();
        model.user.attemptSteal2FAtoken.assertUncompromised();
        model.user.steal2FAtoken.assertUncompromised();

        model.identity.assume.assertUncompromised();
    }

}
