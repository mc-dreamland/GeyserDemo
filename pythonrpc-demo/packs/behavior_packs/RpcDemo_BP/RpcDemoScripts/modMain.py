from mod.common.mod import Mod
import mod.client.extraClientApi as clientApi


@Mod.Binding(name="RpcDemo", version="1.0.1")
class RpcDemoMod(object):
    @Mod.InitClient()
    def init_client(self):
        clientApi.RegisterSystem(
            "RpcDemo", "RpcDemoClientSystem", "RpcDemoScripts.client.RpcDemoClient"
        )
