import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import cat.nyaa.aolib.AoLibPlugin;
import cat.nyaa.aolib.message.AoMessage;
import cat.nyaa.nyaacore.NyaaCoreLoader;
import net.md_5.bungee.api.chat.TextComponent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

public class AoMessageTest {

    private static ServerMock server;
    private static AoLibPlugin plugin;
    private static NyaaCoreLoader nyaacore;

    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
        nyaacore = MockBukkit.load(NyaaCoreLoader.class);
        plugin = MockBukkit.load(AoLibPlugin.class);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void Test1() {
        PlayerMock pm1 = server.addPlayer();
        var aoMsg = AoMessage.getInstance();
        assert aoMsg != null;
        aoMsg.sendMessageTo(pm1.getUniqueId(), "Test233");
        pm1.assertSaid("Test233");
        aoMsg.sendMessageTo(pm1.getUniqueId(), new TextComponent("Test233"));
        pm1.assertSaid(COLOR_CHAR + "f" + "Test233");
        pm1.assertNoMoreSaid();
    }
//
//    @Test
//    public void Test2() throws InterruptedException {
//
//        PlayerMock pm2 = new PlayerMock(server, "Takaranoao", UUID.fromString("bd5850ab-d325-461a-9eed-5c3a5ee0bfac"));
//        var aoMsg = AoMessage.getInstance();
//        assert aoMsg != null;
//        aoMsg.sendMessageTo(pm2.getUniqueId(), "Test233");
//        server.addPlayer(pm2);
//        Thread.sleep(1000);
//        server.getScheduler().performTicks(300L);
//        pm2.assertSaid("Test233");
//        pm2.assertNoMoreSaid();
//    }

}
