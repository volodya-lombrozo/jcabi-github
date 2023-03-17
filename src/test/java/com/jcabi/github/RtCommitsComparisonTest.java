/**
 * Copyright (c) 2013-2023, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import com.jcabi.http.request.FakeRequest;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtCommitsComparison}.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
public final class RtCommitsComparisonTest {

    /**
     * RtCommitsComparison can fetch JSON.
     * @throws Exception If some problem inside
     * @checkstyle MultipleStringLiterals (75 lines)
     * @checkstyle ExecutableStatementCountCheck (75 lines)
     */
    @Test
    public void fetchesJson() throws Exception {
        final String sha = "fffffffffffffffffffffffffffffffffffffffe";
        final String filename = "bar/quux.txt";
        // @checkstyle MagicNumberCheck (3 lines)
        final int additions = 7;
        final int deletions = 2;
        final int changes = 9;
        final String patch = "some diff here";
        // @checkstyle LineLength (3 lines)
        final String bloburl = "https://api.jcabi-github.invalid/johndoe/my-repo/blob/fffffffffffffffffffffffffffffffffffffffe/bar/quux.txt";
        final String rawurl = "https://api.jcabi-github.invalid/johndoe/my-repo/raw/fffffffffffffffffffffffffffffffffffffffe/bar/quux.txt";
        final String contentsurl = "https://api.github.invalid/repos/johndoe/my-repo/contents/bar/quux.txt?ref=fffffffffffffffffffffffffffffffffffffffe";
        final CommitsComparison comparison = new RtCommitsComparison(
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("base_commit", Json.createObjectBuilder())
                    .add("commits", Json.createArrayBuilder())
                    .add(
                        "files",
                        Json.createArrayBuilder()
                            .add(
                                Json.createObjectBuilder()
                                    .add("sha", sha)
                                    .add("filename", filename)
                                    .add("status", "added")
                                    .add("additions", additions)
                                    .add("deletions", deletions)
                                    .add("changes", changes)
                                    .add("patch", patch)
                                    .add("blob_url", bloburl)
                                    .add("raw_url", rawurl)
                                    .add("contents_url", contentsurl)
                                    .build()
                            )
                            .build()
                    )
                    .build().toString()
            ),
            RtCommitsComparisonTest.repo(),
            "6dcb09b5b57875f334f61aebed695e2e4193db51",
            "6dcb09b5b57875f334f61aebed695e2e4193db52"
        );
        final JsonObject json = comparison.json();
        MatcherAssert.assertThat(
            json.getJsonObject("base_commit"), Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            json.getJsonArray("commits"), Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            comparison.files(),
            Matchers.<FileChange>iterableWithSize(1)
        );
        final FileChange.Smart file = new FileChange.Smart(
            comparison.files().iterator().next()
        );
        MatcherAssert.assertThat(file.sha(), Matchers.equalTo(sha));
        MatcherAssert.assertThat(file.filename(), Matchers.equalTo(filename));
        MatcherAssert.assertThat(file.additions(), Matchers.equalTo(additions));
        MatcherAssert.assertThat(file.deletions(), Matchers.equalTo(deletions));
        MatcherAssert.assertThat(file.changes(), Matchers.equalTo(changes));
        MatcherAssert.assertThat(
            file.status(),
            Matchers.equalTo(FileChange.Status.ADDED)
        );
        MatcherAssert.assertThat(
            file.patch(),
            Matchers.equalTo(Optional.of(patch))
        );
    }

    /**
     * Return repo for tests.
     * @return Repository
     */
    private static Repo repo() {
        return new RtGithub().repos()
            .get(new Coordinates.Simple("user", "repo"));
    }

}
