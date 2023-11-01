package wasm;

import io.github.kawamuray.wasmtime.Engine;
import io.github.kawamuray.wasmtime.Linker;
import io.github.kawamuray.wasmtime.Module;
import io.github.kawamuray.wasmtime.Store;
import io.github.kawamuray.wasmtime.wasi.WasiCtx;
import io.github.kawamuray.wasmtime.wasi.WasiCtxBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WasmMain {

    public static void main(String[] args) throws IOException {
        final var stdinPath = Paths.get("./wasm/io/stdin.txt");
        final var stdoutPath = Paths.get("./wasm/io/stdout.txt");

        Files.writeString(stdinPath, "{\"name\": \"John\"}");

        final var engine = new Engine();
        final var wasi = new WasiCtxBuilder()
                .stdin(stdinPath)
                .stdout(stdoutPath)
                .inheritStderr()
                .build();
        final var store = new Store<>(null, engine, wasi);
        final var linker = new Linker(engine);

        WasiCtx.addToLinker(linker);
        linker.module(store, "javy_quickjs_provider_v1", Module.fromBinary(engine, Files.readAllBytes(Paths.get("./wasm/registry/js_provider.wasm"))));
        linker.module(store, "", Module.fromBinary(engine, Files.readAllBytes(Paths.get("./wasm/registry/greet.wasm"))));

        final var fn = linker.get(store, "", "_start").get().func();

        try (engine;wasi;store;linker;fn) {
            fn.call(store);
        }

        System.out.println(Files.readString(stdoutPath));
    }
}
