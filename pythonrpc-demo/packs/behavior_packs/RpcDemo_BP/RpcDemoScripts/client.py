import mod.client.extraClientApi as clientApi


class RpcDemoClient(clientApi.GetClientSystemCls()):
    def __init__(self, namespace, system_name):
        super(RpcDemoClient, self).__init__(namespace, system_name)
        self.engine_namespace = clientApi.GetEngineNamespace()
        self.engine_system = clientApi.GetEngineSystemName()
        self.ListenForEvent(
            "RpcDemo", "RpcDemoServerSystem", "ServerMessage", self, self.on_reply
        )
        self.ListenForEvent(
            self.engine_namespace, self.engine_system, "OnLocalPlayerStopLoading",
            self, self.on_ready
        )
        print("[RpcDemo] Client system ready")

    def on_ready(self, args):
        data = self.CreateEventData()
        data["requestId"] = "demo-1"
        self.NotifyToServer("QueryServerMessage", data)

    def on_reply(self, args):
        if args.get("requestId") != "demo-1":
            return

        tip_key = args.get("tipKey")
        if not isinstance(tip_key, basestring):
            return

        tips = clientApi.GetModConfigJson("modconfigs/geyser_rpc_demo.json")
        text = tips.get(tip_key)
        if not isinstance(text, basestring) or not text:
            print("[RpcDemo] Tip missing in resource pack: {}".format(tip_key))
            return

        if isinstance(text, unicode):
            text = text.encode("utf-8")
        factory = clientApi.GetEngineCompFactory()
        game = factory.CreateGame(clientApi.GetLocalPlayerId())
        if game.SetTipMessage(text):
            print("[RpcDemo] Displayed tip: {}".format(tip_key))
        else:
            print("[RpcDemo] Could not display tip: {}".format(tip_key))

        notify = factory.CreateTextNotifyClient(clientApi.GetLevelId())
        if not notify.SetLeftCornerNotify("[RpcDemo] " + text):
            print("[RpcDemo] Could not show chat notice: {}".format(tip_key))

    def Destroy(self):
        self.UnListenForEvent(
            self.engine_namespace, self.engine_system, "OnLocalPlayerStopLoading",
            self, self.on_ready
        )
        self.UnListenForEvent(
            "RpcDemo", "RpcDemoServerSystem", "ServerMessage", self, self.on_reply
        )
